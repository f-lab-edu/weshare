package com.flab.weshare.domain.party.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.weshare.domain.party.entity.PartyJoin;

public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
}
