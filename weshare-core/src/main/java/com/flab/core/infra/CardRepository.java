package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
