package com.flab.weshare.domain.user.dto;

import com.flab.weshare.domain.user.entity.User;

public record LoginResponse(Long id, String nickName) {
	public static LoginResponse from(User user) {
		return new LoginResponse(user.getId(), user.getNickName());
	}
}
