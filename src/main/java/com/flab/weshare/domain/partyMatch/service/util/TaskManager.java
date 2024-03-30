package com.flab.weshare.domain.partyMatch.service.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
public class TaskManager<T> {
	private Set<T> taskSet = ConcurrentHashMap.newKeySet();

	public boolean addTask(T task) {
		return taskSet.add(task);
	}

	public void removeTask(T task) {
		taskSet.remove(task);
	}
}
