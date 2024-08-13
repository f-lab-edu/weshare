package com.flab.batch.CustomReader;

import java.util.Map;

import org.springframework.util.Assert;

public class ZeroOffsetJpaPagingItemReaderBuilder<T> {
	private int pageSize = 10;
	private Map<String, Object> parameterValues;
	private boolean transacted = true;
	private String queryString;
	private boolean saveState = true;
	private Class<?> entityClass;
	private String name;
	private int maxItemCount = Integer.MAX_VALUE;
	private int currentItemCount;

	public ZeroOffsetJpaPagingItemReaderBuilder() {
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> saveState(boolean saveState) {
		this.saveState = saveState;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> name(String name) {
		this.name = name;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> entityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> currentItemCount(int currentItemCount) {
		this.currentItemCount = currentItemCount;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> parameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> queryString(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public ZeroOffsetJpaPagingItemReaderBuilder<T> transacted(boolean transacted) {
		this.transacted = transacted;
		return this;
	}

	public ZeroOffsetJpaPagingItemReader<T> build() {
		Assert.isTrue(this.pageSize > 0, "pageSize must be greater than zero");
		if (this.saveState) {
			Assert.hasText(this.name, "A name is required when saveState is set to true");
		}

		ZeroOffsetJpaPagingItemReader<T> reader = new ZeroOffsetJpaPagingItemReader();
		reader.setEntityClass(entityClass);
		reader.setQueryString(this.queryString);
		reader.setPageSize(this.pageSize);
		reader.setParameterValues(this.parameterValues);
		reader.setTransacted(this.transacted);
		reader.setCurrentItemCount(this.currentItemCount);
		reader.setMaxItemCount(this.maxItemCount);
		reader.setSaveState(this.saveState);
		reader.setName(this.name);
		return reader;
	}
}
