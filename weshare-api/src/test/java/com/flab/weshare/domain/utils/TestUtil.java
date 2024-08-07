package com.flab.weshare.domain.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.flab.core.entity.Money;
import com.flab.core.entity.Ott;
import com.flab.core.entity.Party;
import com.flab.core.entity.Role;
import com.flab.core.entity.User;

public class TestUtil {
	private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	//User
	public static final Long USER_ID = 1L;
	public static final String EMAIL = "test@gmail.com";
	public static final String PASSWORD = "test1234!";
	public static final String NICKNAME = "테스트";
	public static final String TELEPHONE = "01012341234";
	public static final User savedUser = User
		.builder()
		.email(EMAIL)
		.password(bCryptPasswordEncoder.encode(PASSWORD))
		.nickName(NICKNAME)
		.telephone(TELEPHONE)
		.role(Role.CLIENT)
		.build();

	//Ott
	public static final Long OTT_ID = 1L;
	public static final String OTT_NAME = "WEFLIX";
	public static final int COMMON_FEE = 3000;
	public static final int LEADER_FEE = 2500;
	public static final int MAXIMUM_CAPACITY = 4;
	public static final int MINIMUM_CAPACITY = 1;
	public static final Ott savedOtt = Ott
		.builder()
		.name(OTT_NAME)
		.maximumCapacity(MAXIMUM_CAPACITY)
		.minimumCapacity(MINIMUM_CAPACITY)
		.perDayPrice(new Money(1000))
		.build();

	//Party
	public static final Long PARTY_ID = 1L;
	public static final int PARTY_MAXIMUM_CAPACITY = 3;
	public static final String OTT_ACCOUNT_ID = "test123";
	public static final String OTT_PASSWORD = "test1234!";
	public static final Party savedParty = Party
		.builder()
		.leader(savedUser)
		.ott(savedOtt)
		.capacity(PARTY_MAXIMUM_CAPACITY)
		.ottAccountId(OTT_ACCOUNT_ID)
		.ottAccountPassword(OTT_PASSWORD)
		.build();
}
