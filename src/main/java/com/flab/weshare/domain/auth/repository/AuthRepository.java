package com.flab.weshare.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.auth.entity.RefreshToken;

public interface AuthRepository extends JpaRepository<RefreshToken, Long> {
	boolean existsByToken(String token);
}
