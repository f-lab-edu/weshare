package com.flab.weshare.domain.party.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.dto.PartyJoinRequest;
import com.flab.weshare.domain.party.service.PartyService;
import com.flab.weshare.utils.jwt.JwtAuthentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/party")
public class PartyController {
	private final PartyService partyService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse createParty(@RequestBody @Valid final PartyCreationRequest partyCreationRequest,
		@AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {

		Long generatedPartyId = partyService.generateParty(partyCreationRequest, jwtAuthentication.getId());

		return BaseResponse.created(generatedPartyId);
	}

	@PutMapping("/{partyId}")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse patchPartyCapacity(@PathVariable final Long partyId
		, @RequestBody @Valid final ModifyPartyRequest modifyPartyRequest) {

		partyService.updatePartyDetails(modifyPartyRequest, partyId);

		return BaseResponse.success();
	}

	@PostMapping("/join")
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse createPartyRequest(@RequestBody @Valid final PartyJoinRequest partyJoinRequest,
		@AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {

		Long generatedPartyJoinId = partyService.generatePartyJoin(partyJoinRequest, jwtAuthentication.getId());

		return BaseResponse.created(generatedPartyJoinId);
	}
}
