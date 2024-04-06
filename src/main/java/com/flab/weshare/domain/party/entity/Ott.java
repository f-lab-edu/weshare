package com.flab.weshare.domain.party.entity;

import org.hibernate.annotations.Immutable;

import com.flab.weshare.domain.base.Money;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ott {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ott_id")
	private Long id;
	private String name;

	@Embedded
	private Money perDayPrice;

	private int maximumCapacity;
	private int minimumCapacity;

	@Builder
	private Ott(String name, Money perDayPrice, int maximumCapacity, int minimumCapacity) {
		this.name = name;
		this.perDayPrice = perDayPrice;
		this.maximumCapacity = maximumCapacity;
		this.minimumCapacity = minimumCapacity;
	}

	public boolean isValidCapacity(int capacity) {
		return capacity >= minimumCapacity && capacity <= maximumCapacity;
	}
}
