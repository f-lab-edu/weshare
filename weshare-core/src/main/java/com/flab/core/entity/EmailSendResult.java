package com.flab.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailSendResult {
	public static final EmailSendResult INITIAL = EmailSendResult.builder()
		.emailSentStatus(EmailSentStatus.NOT_SENT)
		.build();

	@Enumerated(EnumType.STRING)
	private EmailSentStatus emailSentStatus;
	private LocalDateTime sentAt; // 이메일 발송 시각
	private String errorMessage; // 발송 실패 시 오류 메시지

	@Builder
	private EmailSendResult(EmailSentStatus emailSentStatus, LocalDateTime sentAt, String errorMessage) {
		this.emailSentStatus = emailSentStatus;
		this.sentAt = sentAt;
		this.errorMessage = errorMessage;
	}

	public void successSent(final LocalDateTime sentAt) {
		if (sentAt == null) {
			throw new IllegalArgumentException("메일 전송 성공했으나 발송시간이 존재하지않음.");
		}
		this.emailSentStatus = EmailSentStatus.SENT_SUCCESS;
		this.sentAt = sentAt;
	}

	public void failSent(final String errorMessage) {
		if (errorMessage == null) {
			throw new IllegalArgumentException("메일 전송에 실패했으나 에러메시지가 존재하지않음.");
		}
		this.emailSentStatus = EmailSentStatus.SENT_FAILURE;
		this.errorMessage = errorMessage;
	}
}
