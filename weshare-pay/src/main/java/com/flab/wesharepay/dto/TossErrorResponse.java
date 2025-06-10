package com.flab.wesharepay.dto;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TossErrorResponse {
	private final String status;
	private final String code;
	private final String message;

	public TossErrorResponse(JSONObject jsonObject) {
		this.status = jsonObject.get("status").toString();
		this.code = jsonObject.get("code").toString();
		this.message = jsonObject.get("message").toString();
	}
}
