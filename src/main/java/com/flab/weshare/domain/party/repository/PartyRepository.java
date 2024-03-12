package com.flab.weshare.domain.party.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Party;

public interface PartyRepository extends JpaRepository<Party, Long> {

	@Query("select p from Party p left join fetch p.partyMembers join fetch p.ott where p.id =:partyId")
	Optional<Party> findFetchByPartyId(@Param("partyId") Long partyId);
}
