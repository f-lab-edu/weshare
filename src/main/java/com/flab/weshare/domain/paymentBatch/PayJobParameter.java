package com.flab.weshare.domain.paymentBatch;

import java.time.LocalDate;

import com.flab.weshare.domain.party.entity.PartyCapsuleStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PayJobParameter {
	private LocalDate payJobDate;
	private LocalDate renewExpirationDate;
	private PartyCapsuleStatus status;

	public PayJobParameter(LocalDate payJobDate, LocalDate renewExpirationDate, PartyCapsuleStatus status) {
		this.payJobDate = payJobDate;
		this.renewExpirationDate = renewExpirationDate;
		this.status = status;
	}
}
