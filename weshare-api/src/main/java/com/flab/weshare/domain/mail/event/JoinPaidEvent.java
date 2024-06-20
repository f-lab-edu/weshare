package com.flab.weshare.domain.mail.event;

import com.flab.core.entity.Money;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class JoinPaidEvent {
	private final Long PartyCapsuleId;
	private final Money paidAmount;
}
