package com.flab.core.entity;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user"})
public class BillingKey extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "billing_key_id")
	private Long id;

	@JsonProperty("mId")
	private String mId; // 상점 아이디
	private OffsetDateTime authenticatedAt; // 인증된 시점
	private String method; // 결제수단 (현재 카드로 고정)
	private String billingKey; // 빌링키
	private String cardCompany; // 카드 발급사
	private String cardNumber; // 카드 번호 (마스킹 처리됨)

	@Enumerated(EnumType.STRING)
	private BillingKeyStatus billingKeyStatus = BillingKeyStatus.ISSUED;

	@ManyToOne
	@Setter
	@JoinColumn(name = "user_id")
	private User user; // 구매자 ID

	@Builder
	public BillingKey(Long id, String mId, OffsetDateTime authenticatedAt, String method, String billingKey,
		String cardCompany, String cardNumber) {
		this.id = id;
		this.mId = mId;
		this.authenticatedAt = authenticatedAt;
		this.method = method;
		this.billingKey = billingKey;
		this.cardCompany = cardCompany;
		this.cardNumber = cardNumber;
	}

}
