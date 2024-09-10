package com.flab.batch.CustomReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ZeroOffsetRepositoryItemReaderBuilder<T> {
	private PagingAndSortingRepository<?, ?> repository;
	private Map<String, Sort.Direction> sorts;
	private List<?> arguments;
	private int pageSize = 10;
	private String methodName;
	private Class<?> idClass;
	private String idFieldName;
	private boolean saveState = true;
	private String name;
	private int maxItemCount = Integer.MAX_VALUE;
	private int currentItemCount;

	public ZeroOffsetRepositoryItemReaderBuilder() {
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> saveState(boolean saveState) {
		this.saveState = saveState;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> name(String name) {
		this.name = name;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> maxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> currentItemCount(int currentItemCount) {
		this.currentItemCount = currentItemCount;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> arguments(List<?> arguments) {
		this.arguments = arguments;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> arguments(Object... arguments) {
		return this.arguments(Arrays.asList(arguments));
	}

	// public ZeroOffsetRepositoryItemReaderBuilder<T> sorts(Map<String, Sort.Direction> sorts) {
	// 	this.sorts = sorts;
	// 	return this;
	// }

	public ZeroOffsetRepositoryItemReaderBuilder<T> pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> repository(PagingAndSortingRepository<?, ?> repository) {
		this.repository = repository;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> methodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

	public ZeroOffsetRepositoryItemReaderBuilder<T> setPkColumn(Class<?> idClass, String idFieldName) {
		this.idClass = idClass;
		this.idFieldName = idFieldName;
		return this;
	}

	public ZeroOffsetRepositoryItemReader<T> build() {
		//Assert.notNull(this.sorts, "sorts map is required.");
		Assert.notNull(this.repository, "repository is required.");
		Assert.notNull(this.idFieldName, "pk_id field name is required.");
		Assert.notNull(this.idClass, "id_class is required.");
		Assert.isTrue(this.pageSize > 0, "Page size must be greater than 0");
		Assert.hasText(this.methodName, "methodName is required.");
		if (this.saveState) {
			Assert.state(StringUtils.hasText(this.name), "A name is required when saveState is set to true.");
		}

		ZeroOffsetRepositoryItemReader<T> reader = new ZeroOffsetRepositoryItemReader();
		reader.setArguments(this.arguments);
		reader.setRepository(this.repository);
		reader.setMethodName(this.methodName);
		reader.setPageSize(this.pageSize);
		reader.setCurrentItemCount(this.currentItemCount);
		reader.setMaxItemCount(this.maxItemCount);
		reader.setSaveState(this.saveState);
		//reader.setSort(this.sorts);
		reader.setIdClass(this.idClass);
		reader.setIdFieldName(this.idFieldName);
		reader.setName(this.name);
		return reader;
	}

}
