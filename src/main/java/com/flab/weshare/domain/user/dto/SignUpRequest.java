package com.flab.weshare.domain.user.dto;

import static com.flab.weshare.utils.RegEx.Message.*;
import static com.flab.weshare.utils.RegEx.Pattern.*;

import com.flab.weshare.domain.user.entity.Role;
import com.flab.weshare.domain.user.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(@Email(message = "이메일 형식에 맞지 않습니다.") String email,
							@Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE) String nickName,
							@Pattern(regexp = TELEPHONE_PATTERN, message = TELEPHONE_MESSAGE) String telephone) {
	public User convert() {

		return User.builder()
			.email(email)
			.telephone(telephone)
			.nickName(nickName)
			.role(Role.CLIENT)
			.build();
	}
}
