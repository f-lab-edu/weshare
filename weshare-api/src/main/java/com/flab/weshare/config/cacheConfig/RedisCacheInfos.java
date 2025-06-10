package com.flab.weshare.config.cacheConfig;

import static com.flab.weshare.config.cacheConfig.CacheNames.*;

import java.time.Duration;

import com.flab.weshare.domain.party.dto.ParticipatedPartyDto;
import com.flab.weshare.domain.party.dto.PartyCapsuleInfo;
import com.flab.weshare.domain.party.dto.PartyInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisCacheInfos {
	PARTY_TOTAL_CACHE(PARTY_TOTAL, Duration.ofMinutes(30), ParticipatedPartyDto.class),
	PARTY_INFO_CACHE(PARTY_INFO, Duration.ofMinutes(30), PartyInfo.class),
	PARTY_CAPSULE_INFO_CACHE(PARTY_CAPSULE_INFO, Duration.ofMinutes(30), PartyCapsuleInfo.class),
	;

	private final String cacheName;
	private final Duration expiredAfterWrite;
	private final Class<?> clazz;
}
