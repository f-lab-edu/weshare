package com.flab.core.entity;

public enum BillingKeyStatus {
	ISSUED("발급됨"),
	UNISSUED("미발급"),
	UNAVAIABLE("사용할수없음");

	private final String description;

	BillingKeyStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
