package com.flab.weshare.domain.user.entity;

import com.flab.weshare.domain.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
@Getter
public class User extends BaseEntity {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickName;
	private String telephone;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private User(String email, String password, String nickName, String telephone, Role role) {
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.telephone = telephone;
		this.role = role;
	}
}
