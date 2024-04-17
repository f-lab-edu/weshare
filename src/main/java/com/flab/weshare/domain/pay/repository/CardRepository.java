package com.flab.weshare.domain.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.pay.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
