package com.flab.wesharepay.service;

public record CardInfo(
	String cardNumber,
	String cardPassword,
	String cardExpireYear,
	String cardExpireMonth,
	String birthDate) {
}

