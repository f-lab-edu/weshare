package com.flab.weshare.domain.base;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class Base64Test {
	@Test
	public void encode() {
		UUID uuid = UUID.randomUUID();
		byte[] bytes = ByteBuffer.allocate(16)
			.putLong(uuid.getMostSignificantBits())
			.putLong(uuid.getLeastSignificantBits())
			.array();

		String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		System.out.println(base64); // 보통 22자짜리 문자열
	}
}
