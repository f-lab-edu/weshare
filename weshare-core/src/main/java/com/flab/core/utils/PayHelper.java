package com.flab.core.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.flab.core.entity.Money;

public class PayHelper {
	static final int SCALE = 1;

	public static LocalDateTime getNextExpirationDate(LocalDateTime startDate) {
		return startDate.plusMonths(1);
	}

	public static Money calculateMoney(LocalDateTime expirationDate, LocalDateTime updatedExpiredDate, Money mul) {
		if (updatedExpiredDate.isBefore(expirationDate)) {
			throw new IllegalArgumentException("updatedExpiredDate must be after expirationDate");
		}

		if (!updatedExpiredDate.toLocalTime().equals(expirationDate.toLocalTime())) {
			throw new IllegalArgumentException("time not match");
		}

		long betweenDays = ChronoUnit.DAYS.between(expirationDate, updatedExpiredDate);
		return mul.multiply(betweenDays);
	}
}
