package com.flab.weshare.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	boolean existsByNickName(String nickName);

	// @Query("select new com.flab.weshare.domain.user.dto.LoginResponse(u.id, u.nickName)"
	// 	+ " from User u"
	// 	+ " where u.email= :email and u.password= :password"
	// )
	Optional<User> findByEmailAndPassword(String email, String password);
}
