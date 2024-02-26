package com.flab.weshare.domain.auth.dto;

import com.flab.weshare.domain.user.entity.Role;
import com.flab.weshare.domain.user.entity.User;

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
