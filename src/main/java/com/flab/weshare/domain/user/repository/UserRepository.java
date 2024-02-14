package com.flab.weshare.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	boolean existsByNickName(String nickName);
}
