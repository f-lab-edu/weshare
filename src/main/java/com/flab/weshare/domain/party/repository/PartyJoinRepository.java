package com.flab.weshare.domain.party.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.PartyJoin;

import jakarta.persistence.LockModeType;

public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
	@Query("select pj "
		+ "from PartyJoin pj "
		+ "where pj.ott =:ott and pj.partyJoinStatus = com.flab.weshare.domain.party.entity.PartyJoinStatus.WAITING "
		+ "order by pj.createdDate asc")
	List<PartyJoin> findWaitingPartyJoinByOtt(@Param("ott") Ott ott);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select pj "
		+ "from PartyJoin pj "
		+ "where pj.id =:partyJoinId")
	Optional<PartyJoin> findByIdForUpdate(@Param("partyJoinId") Long partyJoinId);
}
