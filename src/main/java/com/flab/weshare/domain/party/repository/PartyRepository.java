package com.flab.weshare.domain.party.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.user.entity.User;

public interface PartyRepository extends JpaRepository<Party, Long> {
	@Query("select distinct p "
		+ "from Party p "
		+ "join fetch p.partyCapsules pc "
		+ "join fetch p.ott "
		+ "where p.id =:partyId "
		+ "and pc.partyCapsuleStatus in (com.flab.weshare.domain.party.entity.PartyCapsuleStatus.EMPTY,"
		+ "com.flab.weshare.domain.party.entity.PartyCapsuleStatus.OCCUPIED)")
	Optional<Party> findFetchByPartyId(@Param("partyId") Long partyId);

	@Query("select p "
		+ "from Party p "
		+ "join fetch p.ott "
		+ "where p.leader =:user")
	List<Party> findByUserIdWithOtt(@Param("user") User user);
}
