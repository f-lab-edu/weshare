package com.flab.batch.paymentBatch;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.flab.batch.paymentBatch.exception.CalculatePayException;
import com.flab.core.entity.Money;

@Component
public class PriceCalculatePolicy {
	private static final int MAXIMUM_DAYS = 62;

	public Money calculatePrice(LocalDate expDate, LocalDate targetDate, Money perDayPrice) {
		if (targetDate.isBefore(expDate)) {
			throw new CalculatePayException("목표 일자보다 결제일이 뒤쳐졌습니다.");
		}

		long daysBetween = ChronoUnit.DAYS.between(expDate, targetDate);

		if (daysBetween > MAXIMUM_DAYS) {
			throw new CalculatePayException("결제 일수가 허용 일수보다 큽니다.");
		}
		return perDayPrice.multiply(daysBetween);
	}
}
