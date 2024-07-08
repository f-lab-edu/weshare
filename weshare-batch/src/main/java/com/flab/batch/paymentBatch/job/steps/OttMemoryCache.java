package com.flab.batch.paymentBatch.job.steps;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.flab.core.entity.Money;
import com.flab.core.entity.Ott;

@Component
public class OttMemoryCache {
	private final Map<Long, Ott> cache = new HashMap<>();

	public void addOtt(final Ott ott) {
		cache.put(ott.getId(), ott);
	}

	public Money getPerDayPriceById(final Long ottId) {
		return cache.get(ottId).getPerDayPrice();
	}
}
