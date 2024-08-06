package com.flab.core.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
	List<Card> findByIdBetween(Long startId, Long endId);
}
