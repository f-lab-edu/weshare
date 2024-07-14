package com.flab.wesharepay.service;

import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Receipt {
	private final HashMap<String, Object> receipt;
	private final PayStatus payStatus;

	public static Receipt successReceipt(HashMap<String, Object> receipt) {
		return new Receipt(receipt, PayStatus.SUCCESS);
	}

	public static Receipt failReceipt(HashMap<String, Object> receipt) {
		return new Receipt(receipt, PayStatus.FAILED);
	}

	public boolean isSuccess() {
		return payStatus.equals(PayStatus.SUCCESS);
	}
}


