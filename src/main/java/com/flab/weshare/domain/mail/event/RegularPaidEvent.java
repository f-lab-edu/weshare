package com.flab.weshare.domain.mail.event;

import com.flab.weshare.domain.base.Money;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class RegularPaidEvent {
	private final Long PartyCapsuleId;//정기결제를 수행한 파티 캡슐의 id
	private final Money paidAmount;//정기결제를 수행한 파티 캡슐의 id
}
