package com.flab.weshare.domain.party.repository;

import org.springframework.beans.factory.annotation.Autowired;

import com.flab.weshare.domain.base.BaseRepositoryTest;

import jakarta.persistence.EntityManager;

class PartyJoinRepositoryTest extends BaseRepositoryTest {
	@Autowired
	PartyJoinRepository partyJoinRepository;

	@Autowired
	EntityManager em;

	// @Test
	// void findWaitingPartyJoinByOtt() {
	// 	em.clear();
	// 	List<PartyJoin> partyJoin = createPartyJoin();
	//
	// 	PartyJoin.builder()
	// 		.user(userRepository.getReferenceById(1L));
	// 	List<PartyJoin> waitingPartyJoinByOtt = partyJoinRepository.findWaitingPartyJoinByOtt(testOtt);
	//
	// 	Assertions.assertThat(waitingPartyJoinByOtt).hasSize(partyJoin.size());
	// }
	//
	// private List<PartyJoin> createPartyJoin() {
	// 	List<PartyJoin> partyJoins = new ArrayList<>();
	// 	for (int i = 1; i < 6; i++) {
	// 		PartyJoin partyJoin = PartyJoin.genertaeWaitingPartyJoin(userRepository.getReferenceById((long)i), testOtt);
	// 		partyJoins.add(partyJoin);
	// 		partyJoinRepository.save(partyJoin);
	// 	}
	//
	// 	for (int i = 6; i < 10; i++) {
	// 		PartyJoin partyJoin = PartyJoin.builder()
	// 			.user(userRepository.getReferenceById((long)i))
	// 			.ott(testOtt)
	// 			.partyJoinStatus(PartyJoinStatus.JOINED)
	// 			.build();
	// 		partyJoinRepository.save(partyJoin);
	// 	}
	// 	return partyJoins;
	// }

}
