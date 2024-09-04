package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.PartySign;

public interface PartySignRepository extends JpaRepository<PartySign, Long> {
}
