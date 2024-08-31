package com.flab.core.entity;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayRes extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pay_res_id")
	private Long id;

	@OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private PayResStatus payResultStatus;
	private LocalDateTime payApprovedAt;
	private String errorMessage;
	private String receipt;

	@Builder
	public PayRes(Long id, User user, PayResStatus payResultStatus, String errorMessage, String receipt,
		LocalDateTime payApprovedAt) {
		this.id = id;
		this.payApprovedAt = payApprovedAt;
		this.user = user;
		this.payResultStatus = payResultStatus;
		this.receipt = receipt;
		this.errorMessage = errorMessage;
	}

	public static PayRes success(User user, JSONObject response, LocalDateTime payApprovedAt) {
		return PayRes.builder()
			.user(user)
			.payApprovedAt(payApprovedAt)
			.payResultStatus(PayResStatus.SUCCESS)
			.receipt(response.toJSONString())
			.build();
	}

	public static PayRes fail(User user, String response) {
		return PayRes.builder()
			.user(user)
			.payResultStatus(PayResStatus.FAILED)
			.errorMessage(response)
			.build();
	}

	public boolean isSuccess() {
		return this.payResultStatus.equals(PayResStatus.SUCCESS);
	}
}
