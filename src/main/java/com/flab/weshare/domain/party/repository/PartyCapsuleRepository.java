package com.flab.weshare.domain.party.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.PartyCapsule;

import jakarta.persistence.LockModeType;

public interface PartyCapsuleRepository extends JpaRepository<PartyCapsule, Long> {
	@Query("select pc "
		+ "from PartyCapsule pc "
		+ "where pc.party.ott =:ott and pc.partyCapsuleStatus = com.flab.weshare.domain.party.entity.PartyCapsuleStatus.EMPTY "
		+ "order by pc.createdDate asc")
	List<PartyCapsule> findEmptyCapsuleByOtt(@Param("ott") Ott ott);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select pc "
		+ "from PartyCapsule pc "
		+ "where pc.id =:partyCapsuleId")
	Optional<PartyCapsule> findByIdForUpdate(@Param("partyCapsuleId") Long partyCapsuleId);
}
