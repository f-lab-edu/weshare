package com.flab.weshare.domain.party.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Immutable
@Getter
public class Ott {
	@Id
	@Column(name = "ott_id")
	private Long id;
	private String name;
	private int commonFee;
	private int leaderFee;
	private int maximumCapacity;
	private int minimumCapacity;

	public boolean isValidCapacity(int capacity) {
		return capacity >= minimumCapacity && capacity <= maximumCapacity;
	}
}
