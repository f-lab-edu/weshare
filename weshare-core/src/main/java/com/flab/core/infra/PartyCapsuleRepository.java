package com.flab.core.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.core.entity.Ott;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.User;

import jakarta.persistence.LockModeType;

public interface PartyCapsuleRepository extends JpaRepository<PartyCapsule, Long> {
	@Query("select pc "
		+ "from PartyCapsule pc "
		+ "where pc.party.ott =:ott and pc.partyCapsuleStatus = com.flab.core.entity.PartyCapsuleStatus.EMPTY "
		+ "order by pc.createdDate asc")
	List<PartyCapsule> findEmptyCapsuleByOtt(@Param("ott") Ott ott);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select pc "
		+ "from PartyCapsule pc "
		+ "where pc.id =:partyCapsuleId")
	Optional<PartyCapsule> findByIdForUpdate(@Param("partyCapsuleId") Long partyCapsuleId);

	@Query(value = "select pc "
		+ "from PartyCapsule pc "
		+ "join fetch pc.party pcp "
		+ "join fetch pcp.ott "
		+ "join fetch pc.partyMember pm "
		+ "where pc.partyCapsuleStatus =:status "
		, countQuery = "select count(pc) from PartyCapsule pc where pc.partyCapsuleStatus =:status")
	Page<PartyCapsule> findFetchPartyCapsuleByStatus(@Param("status") PartyCapsuleStatus partyCapsuleStatus,
		Pageable pageable);

	@Modifying
	@Query("update PartyCapsule pc "
		+ "set pc.expirationDate = :expirationDate "
		+ "where pc in :partyCapsules")
	void updateAllExpirationDate(@Param("expirationDate") LocalDateTime expirationDate,
		@Param("partyCapsules") List<PartyCapsule> partyCapsules);

	@Query(value = "select pc "
		+ "from PartyCapsule pc "
		+ "join fetch pc.party pcp "
		+ "join fetch pcp.ott "
		+ "where pc.partyMember =:user and pc.partyCapsuleStatus =:status")
	List<PartyCapsule> findAllPartyCapsulesUserId(@Param("status") PartyCapsuleStatus partyCapsuleStatus,
		@Param("user") User user);

	@Query(value = "select pc "
		+ "from PartyCapsule pc "
		+ "join fetch pc.party pcp "
		+ "join fetch pcp.ott "
		+ "where pc.id =:id")
	Optional<PartyCapsule> findPartyCapsuleById(@Param("id") Long partyCapsuleId);

	@Query(value = "select pc "
		+ "from PartyCapsule pc "
		+ "join fetch pc.partyMember pm "
		+ "where pc.id =:partyCapsuleId")
	Optional<PartyCapsule> findByIdForFetchAll(@Param("partyCapsuleId") Long partyCapsuleId);
}
