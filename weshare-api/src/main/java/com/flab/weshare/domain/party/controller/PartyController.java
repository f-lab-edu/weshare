package com.flab.weshare.domain.party.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.party.dto.LeadingPartySummary;
import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.ParticipatedPartyDto;
import com.flab.weshare.domain.party.dto.ParticipatingPartySummary;
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

	@GetMapping("/my")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse getAllParticipatedParties(@AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {
		ParticipatedPartyDto allParticipatedParties = partyService.findAllParticipatedParties(
			jwtAuthentication.getId());

		List<EntityModel<LeadingPartySummary>> result1 = allParticipatedParties.getLeadingParties()
			.stream().map(leadingPartySummary ->
				EntityModel.of(leadingPartySummary, linkTo(
					methodOn(PartyController.class).getParty(leadingPartySummary.partyId(), null)).withSelfRel()))
			.toList();

		List<EntityModel<ParticipatingPartySummary>> result2 = allParticipatedParties.getParticipatingParties()
			.stream().map(participatingPartySummary ->
				EntityModel.of(participatingPartySummary, linkTo(
					methodOn(PartyController.class).getPartyCapsule(participatingPartySummary.partyCapsuleId(),
						null)).withSelfRel()))
			.toList();

		EntityModel<Map<String, List<? extends EntityModel<? extends Record>>>> data = EntityModel.of(
			Map.of("leadingParties", result1, "participatingParties", result2),
			linkTo(methodOn(PartyController.class).getAllParticipatedParties(null)).withSelfRel());

		return BaseResponse.success(data);
	}

	@GetMapping("/{partyId}")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse getParty(@PathVariable final Long partyId,
		@AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {
		return BaseResponse.success(partyService.getPartyInfo(partyId, jwtAuthentication.getId()));
	}

	@GetMapping("/participated/{partyCapsuleId}")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse getPartyCapsule(
		@PathVariable final Long partyCapsuleId, @AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {
		return BaseResponse.success(partyService.getPartyCapsuleInfo(partyCapsuleId, jwtAuthentication.getId()));
	}
}
