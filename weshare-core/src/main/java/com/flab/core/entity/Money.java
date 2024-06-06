package com.flab.core.entity;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable
public class Money {
	private final BigDecimal amount;

	public Money() {
		this.amount = BigDecimal.ZERO;
	}

	public Money(long amount) {
		this.amount = BigDecimal.valueOf(amount);
	}

	public Money(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getIntegerAmount() {
		return amount.intValue();
	}

	public Money add(Money money) {
		return new Money(amount.add(money.amount));
	}

	public Money minus(Money money) {
		return new Money(amount.subtract(money.amount));
	}

	public Money multiply(Number number) {
		return new Money(amount.multiply(BigDecimal.valueOf(number.intValue())));
	}
}
