package com.flab.batch.CustomReader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code ZeroOffsetRepositoryItemReader}는 기존 {@code RepositoryItemReader}가 가지고 있는 페이징 쿼리에서 발생하는 성능적
 * 한계점을 극복하기 위해 개발되었습니다.
 * 기본적인 골조는 {@code RepositoryItemReader}를 원본으로 하여 목표로하는 페이징 호출 로직을 수행하도록 기존 코드를 수정해나가는 식으로 작성되었습니다.
 */
@Setter
@Slf4j
public class ZeroOffsetRepositoryItemReader<T> extends AbstractItemCountingItemStreamItemReader<T>
	implements InitializingBean {
	protected Log logger = LogFactory.getLog(this.getClass());
	private PagingAndSortingRepository<?, ?> repository;
	private Sort sort;
	private volatile int page = 0;
	private int pageSize = 10;
	private volatile int current = 0;
	private Class<?> idClass;
	private volatile Object lastId = 0L;
	private List<?> arguments;
	private volatile List<T> results;
	private final Object lock = new Object();
	private String idFieldName;
	private String methodName;

	public ZeroOffsetRepositoryItemReader() {
		this.setName(ClassUtils.getShortName(ZeroOffsetRepositoryItemReader.class));
	}

	public void afterPropertiesSet() throws Exception {
		Assert.state(this.repository != null, "A PagingAndSortingRepository is required");
		Assert.state(this.pageSize > 0, "Page size must be greater than 0");
		//Assert.state(this.sort != null, "A sort is required");
		Assert.state(this.methodName != null && !this.methodName.isEmpty(), "methodName is required.");
		if (this.isSaveState()) {
			Assert.state(StringUtils.hasText(this.getName()), "A name is required when saveState is set to true.");
		}

		//엔티티의 id 타입 동적 반영
		if (this.idClass.equals(String.class)) {
			this.lastId = "";
		}

		this.sort = Sort.by(new Sort.Order(Sort.Direction.ASC, idFieldName));

	}

	/**
	 * 설명: 첫 질의, 다음 페이지로 넘어가야하는 상황은 {@code doPageRead()}를 호출함
	 *
	 * @return 질의 결과의 개별 row
	 */
	@Nullable
	protected T doRead() throws Exception {
		synchronized (this.lock) {
			// 현재 초기 요청이 아니거나 페이지 전체 사이즈보다 탐새하는 인덱스가 작을 경우  false 반환
			boolean nextPageNeeded = this.results != null && this.current >= this.results.size();
			if (this.results == null || nextPageNeeded) { //다음 페이지 요청이 필요한 상황이면
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Reading page " + this.page);
				}

				if (nextPageNeeded) {
					this.lastId = extractLastIdValue(results.get(results.size() - 1));
					this.current = 0;
				}

				//이부분추가.
				this.results = this.doPageRead();
				if (this.results.isEmpty()) {
					return null;
				}
			}

			if (this.current < this.results.size()) {
				T curLine = this.results.get(this.current);
				++this.current;
				return curLine;
			} else {
				return null;
			}
		}
	}

	/**
	 * id 필드의 값을 추출
	 */
	private Object extractLastIdValue(T curLine) throws IllegalAccessException {
		Field id = ReflectionUtils.findField(curLine.getClass(), "id");
		ReflectionUtils.makeAccessible(id);
		return id.get(curLine);
	}

	protected void jumpToItem(int itemLastIndex) throws Exception {
		synchronized (this.lock) {
			this.page = itemLastIndex / this.pageSize;
			this.current = itemLastIndex % this.pageSize;
		}
	}

	protected List<T> doPageRead() throws Exception {
		Pageable pageRequest = PageRequest.of(0, this.pageSize); //zero offset 적용
		MethodInvoker invoker = this.createMethodInvoker(this.repository, this.methodName);
		List<Object> parameters = new ArrayList();
		if (this.arguments != null && !this.arguments.isEmpty()) {
			parameters.addAll(this.arguments);
		}
		parameters.add(lastId); //where 절 마지막에 Last id 추가
		parameters.add(pageRequest);
		invoker.setArguments(parameters.toArray());
		Slice<T> curPage = (Slice)this.doInvoke(invoker);
		return curPage.getContent();
	}

	protected void doOpen() throws Exception {
	}

	protected void doClose() throws Exception {
		synchronized (this.lock) {
			this.current = 0;
			this.page = 0;
			this.results = null;
		}
	}

	private Sort convertToSort(Map<String, Sort.Direction> sorts) {
		List<Sort.Order> sortValues = new ArrayList();
		Iterator var3 = sorts.entrySet().iterator();

		while (var3.hasNext()) {
			Map.Entry<String, Sort.Direction> curSort = (Map.Entry)var3.next();
			sortValues.add(new Sort.Order((Sort.Direction)curSort.getValue(), (String)curSort.getKey()));
		}
		return Sort.by(sortValues);
	}

	private Object doInvoke(MethodInvoker invoker) throws Exception {
		try {
			invoker.prepare();
		} catch (NoSuchMethodException | ClassNotFoundException var3) {
			ReflectiveOperationException e = var3;
			throw new DynamicMethodInvocationException(e);
		}

		try {
			return invoker.invoke();
		} catch (InvocationTargetException var4) {
			InvocationTargetException e = var4;
			if (e.getCause() instanceof Exception) {
				throw (Exception)e.getCause();
			} else {
				throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
			}
		} catch (IllegalAccessException var5) {
			IllegalAccessException e = var5;
			throw new DynamicMethodInvocationException(e);
		}
	}

	private MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
		MethodInvoker invoker = new MethodInvoker();
		invoker.setTargetObject(targetObject);
		invoker.setTargetMethod(targetMethod);
		return invoker;
	}
}
