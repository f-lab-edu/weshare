package com.flab.weshare.domain.party.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.flab.weshare.domain.base.BaseRepositoryTest;
import com.flab.weshare.domain.party.entity.Party;

import jakarta.persistence.EntityManager;

class PartyRepositoryTest extends BaseRepositoryTest {
	@Autowired
	EntityManager em;

	@Test
	void fetchTest() {
		em.clear();
		Optional<Party> fetchByPartyId = partyRepository.findFetchByPartyId(savedParty.getId());
		Party party = fetchByPartyId.get();

		assertThat(fetchByPartyId)
			.isNotEmpty();
		assertThat(party.getCapsulesSize()).isEqualTo(3);
		assertThat(party.countOccupiedPartyCapsule()).isEqualTo(2);
	}
}
