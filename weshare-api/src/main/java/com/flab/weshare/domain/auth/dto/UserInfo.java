package com.flab.weshare.domain.auth.dto;

import com.flab.core.entity.Role;
import com.flab.core.entity.User;

public record UserInfo(Long id,
					   String email,
					   String nickName,
					   Role role) {
	public static UserInfo from(User user) {
		return new UserInfo(
			user.getId()
			, user.getEmail()
			, user.getNickName()
			, user.getRole());
	}
}
