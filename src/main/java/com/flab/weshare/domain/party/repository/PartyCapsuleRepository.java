package com.flab.weshare.domain.party.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.user.entity.User;

public interface PartyCapsuleRepository extends JpaRepository<PartyCapsule, Long> {
	@Query("select pc "
		+ "from PartyCapsule pc "
		+ "where pc.party.ott =:ott and pc.partyCapsuleStatus = com.flab.weshare.domain.party.entity.PartyCapsuleStatus.EMPTY "
		+ "order by pc.createdDate asc")
	List<PartyCapsule> findEmptyCapsuleByOtt(@Param("ott") Ott ott);

	@Modifying
	@Query("update PartyCapsule pc "
		+ "set pc.partyMember=:user , pc.partyCapsuleStatus = com.flab.weshare.domain.party.entity.PartyCapsuleStatus.OCCUPIED"
		+ " where pc=:partyCapsule")
	void updatePartyCapusuleOccupy(@Param("user") User user, @Param("partyCapsule") PartyCapsule partyCapsule);
}
