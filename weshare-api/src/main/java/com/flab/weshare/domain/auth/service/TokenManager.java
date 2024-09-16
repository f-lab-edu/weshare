package com.flab.weshare.domain.auth.service;

public interface TokenManager {
	void saveToken(Long id, String token);

	void validateToken(Long id, String token);

	void removeToken(Long id);
}
