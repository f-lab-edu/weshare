package com.flab.weshare.domain.party.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.PartyJoin;

public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
	@Query("select pj "
		+ "from PartyJoin pj "
		+ "where pj.ott =:ott and pj.partyJoinStatus = com.flab.weshare.domain.party.entity.PartyJoinStatus.WAITING "
		+ "order by pj.createdDate asc")
	List<PartyJoin> findWaitingPartyJoinByOtt(@Param("ott") Ott ott);

	@Modifying
	@Query("update PartyJoin pj "
		+ "set pj.partyJoinStatus= com.flab.weshare.domain.party.entity.PartyJoinStatus.PAY_WAITING"
		+ " where pj=:partyJoin")
	void updatePartyJoinPayWaiting(@Param("partyJoin") PartyJoin partyJoin);
}
