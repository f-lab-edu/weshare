package com.flab.weshare.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AesBytesEncryptUtil {
	private final AesBytesEncryptor aesBytesEncryptor;

	public String encrypt(String target) {
		byte[] encrypt = aesBytesEncryptor.encrypt(target.getBytes(StandardCharsets.UTF_8));
		return byteArrayToString(encrypt);
	}

	public String decrypt(String target) {
		byte[] decryptBytes = stringToByteArray(target);
		byte[] decrypt = aesBytesEncryptor.decrypt(decryptBytes);
		return new String(decrypt, StandardCharsets.UTF_8);
	}

	private String byteArrayToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte abyte : bytes) {
			sb.append(abyte);
			sb.append(" ");
		}
		return sb.toString();
	}

	private byte[] stringToByteArray(String byteString) {
		String[] split = byteString.split("\\s");
		ByteBuffer buffer = ByteBuffer.allocate(split.length);
		for (String s : split) {
			buffer.put((byte)Integer.parseInt(s));
		}
		return buffer.array();
	}
}
