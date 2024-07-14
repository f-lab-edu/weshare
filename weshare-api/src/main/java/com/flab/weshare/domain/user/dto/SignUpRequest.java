package com.flab.weshare.domain.user.dto;

import static com.flab.weshare.utils.RegEx.Message.*;
import static com.flab.weshare.utils.RegEx.Pattern.*;

import com.flab.core.entity.Role;
import com.flab.core.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(@NotNull @Email(message = "이메일 형식에 맞지 않습니다.") String email,
							@NotNull @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_MESSAGE) String password,
							@NotNull @Pattern(regexp = NICKNAME_PATTERN, message = NICKNAME_MESSAGE) String nickName,
							@NotNull @Pattern(regexp = TELEPHONE_PATTERN, message = TELEPHONE_MESSAGE) String telephone) {
	public User convert(String encodedPassword) {

		return User.builder()
			.email(email)
			.telephone(telephone)
			.password(encodedPassword)
			.nickName(nickName)
			.role(Role.CLIENT)
			.build();
	}
}
