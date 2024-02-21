package com.flab.weshare.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByNickName(String nickName);

	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);
}
