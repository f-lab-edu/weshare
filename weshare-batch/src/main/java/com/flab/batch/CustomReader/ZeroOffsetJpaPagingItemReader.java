package com.flab.batch.CustomReader;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
public class ZeroOffsetJpaPagingItemReader<T> extends AbstractPagingItemReader<T> {
	private static final String PK_PARAMETER_NAME = "lastPkId";

	@Autowired
	private EntityManager entityManager;
	private Class<?> entityClass;
	private Field pkField;
	private String modifiedQueryString;
	private String queryString;
	private Map<String, Object> parameterValues;
	private boolean transacted = true;
	private volatile Object lastPkId;

	public ZeroOffsetJpaPagingItemReader() {
		this.setName(ClassUtils.getShortName(JpaPagingItemReader.class));
	}

	private void modifyQueryString() {
		StringBuilder modifiedQueryStringBuilder = new StringBuilder();

		String pkAlias = extractAlias(this.queryString) + "." + pkField.getName();
		log.info("pkAlias = {}", pkAlias);
		String newClause = pkAlias + " >:" + PK_PARAMETER_NAME;

		String lowerJpql = this.queryString.toLowerCase();
		int whereIndex = lowerJpql.indexOf(" where ");
		int orderByIndex = lowerJpql.indexOf(" order by ");
		int groupByIndex = lowerJpql.indexOf(" group by ");
		int endIndex = lowerJpql.length();

		if (groupByIndex != -1 && orderByIndex == -1) {
			endIndex = groupByIndex;
		} else if (orderByIndex != -1) {
			throw new IllegalArgumentException("order by는 지원되지 않습니다.");
		}

		if (whereIndex == -1) {
			// `WHERE` 절이 없는 경우
			String fromClause = this.queryString.substring(0, endIndex);
			String remainingClause = this.queryString.substring(endIndex);
			modifiedQueryStringBuilder.append(fromClause)
				.append(" WHERE ")
				.append(newClause)
				.append(remainingClause);
		} else {
			// `WHERE` 절이 있는 경우
			String beforeWhere = this.queryString.substring(0, whereIndex + 7);
			String afterWhere = this.queryString.substring(whereIndex + 7, endIndex);
			String remainingClause = this.queryString.substring(endIndex);

			modifiedQueryStringBuilder.append(beforeWhere)
				.append(afterWhere)
				.append(" AND ")
				.append(newClause)
				.append(remainingClause);
		}

		modifiedQueryStringBuilder.append(" order by ")
			.append(pkAlias);

		log.info("modifiedQueryString = {}", modifiedQueryStringBuilder.toString());

		this.modifiedQueryString = modifiedQueryStringBuilder.toString();
	}

	private String extractAlias(String jpql) {
		String lowerJpql = jpql.toLowerCase();
		int fromIndex = lowerJpql.indexOf(" from ");

		StringBuilder alias = new StringBuilder();
		int spaceCount = 0;
		for (int i = fromIndex; i < lowerJpql.length(); i++) {
			char c = jpql.charAt(i);
			if (c == ' ') {
				spaceCount++;
				if (spaceCount > 3) {
					break;
				}
				continue;
			}

			if (spaceCount == 3) {
				alias.append(c);
			}
		}
		return alias.toString();
	}

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
	}

	protected void doOpen() throws Exception {
		super.doOpen();
		if (this.entityManager == null) {
			throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
		}
		setPkColumInfo();
		modifyQueryString();
		initFirstIdValue();
	}

	protected void doClose() throws Exception {
		super.doClose();
		this.pkField.setAccessible(false);
	}

	private void initFirstIdValue() {
		if (Number.class.isAssignableFrom(pkField.getType())) {
			lastPkId = -1;
		} else if (String.class.isAssignableFrom(pkField.getType())) {
			lastPkId = "";
		} else {
			throw new IllegalArgumentException("지원하지않는 Pk 필드 타입입니다.");
		}
	}

	private void setPkColumInfo() {
		for (Field field : entityClass.getDeclaredFields()) {
			Id myAnnotation = field.getAnnotation(Id.class);
			if (myAnnotation != null) {
				this.pkField = field;
				this.pkField.setAccessible(true);
				break;
			}
		}
	}

	@Transactional
	protected void doReadPage() {
		Query query = this.entityManager.createQuery(this.modifiedQueryString)
			.setFirstResult(0)
			.setMaxResults(this.getPageSize());

		if (this.parameterValues != null) {
			Iterator var3 = this.parameterValues.entrySet().iterator();

			while (var3.hasNext()) {
				Map.Entry<String, Object> me = (Map.Entry)var3.next();
				query.setParameter((String)me.getKey(), me.getValue());
			}
		}
		query.setParameter(PK_PARAMETER_NAME, lastPkId);

		if (this.results == null) {
			this.results = new CopyOnWriteArrayList();
		} else {
			this.results.clear();
		}

		if (!this.transacted) {
			List<T> queryResult = query.getResultList();
			Iterator var7 = queryResult.iterator();

			while (var7.hasNext()) {
				T entity = (T)var7.next();
				this.entityManager.detach(entity);
				this.results.add(entity);
			}
		} else {
			this.results.addAll(query.getResultList());
			if (!this.results.isEmpty()) {
				lastPkId = extractLastPkIdFromResult();
			}
		}
	}

	private Object extractLastPkIdFromResult() {
		T lastRow = this.results.get(this.results.size() - 1);
		try {
			return pkField.get(lastRow);
		} catch (Exception e) {
			log.error("id capture error", e);
		}
		return null;
	}
}
