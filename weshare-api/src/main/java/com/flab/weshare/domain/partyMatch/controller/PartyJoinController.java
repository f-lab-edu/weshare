package com.flab.weshare.domain.partyMatch.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.partyMatch.dto.JoinResultDto;
import com.flab.weshare.domain.partyMatch.service.PartyJoinService;
import com.flab.weshare.domain.partyMatch.service.wrapper.Accepted;
import com.flab.weshare.domain.partyMatch.service.wrapper.JoinResult;
import com.flab.weshare.domain.partyMatch.service.wrapper.Queued;
import com.flab.weshare.utils.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/partyJoinRequests")
public class PartyJoinController {
	private final PartyJoinService partyJoinService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse createPartyJoinRequest(@RequestParam final Long ott,
		@AuthenticationPrincipal final JwtAuthentication jwtAuthentication) throws IOException {
		JoinResult joinResult = partyJoinService.processJoinRequest(ott, jwtAuthentication.getId());

		if (joinResult instanceof Accepted accepted) {
			return BaseResponse.success(JoinResultDto.ofAccepted(accepted.partyCapsule().getId()));
		} else if (joinResult instanceof Queued queued) {
			return BaseResponse.success(JoinResultDto.ofQueued(queued.partyJoin().getId()));
		} else {
			throw new RuntimeException("Unknown join result type");
		}
	}
}
