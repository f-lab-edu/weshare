package com.flab.weshare.localBatchTest;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.flab.core.entity.Card;
import com.flab.core.entity.Ott;
import com.flab.core.entity.Party;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.Role;
import com.flab.core.entity.User;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartyRepository;
import com.flab.core.infra.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
@Profile("batchTest")
public class BatchDataGenerator {
	private final UserRepository userRepository;
	private final PartyRepository partyRepository;
	private final PartyCapsuleRepository partyCapsuleRepository;
	private final CardRepository cardRepository;
	private final OttRepository ottRepository;

	public BatchDataGenerator(UserRepository userRepository, PartyRepository partyRepository,
		PartyCapsuleRepository partyCapsuleRepository, CardRepository cardRepository, OttRepository ottRepository) {
		this.userRepository = userRepository;
		this.partyRepository = partyRepository;
		this.partyCapsuleRepository = partyCapsuleRepository;
		this.cardRepository = cardRepository;
		this.ottRepository = ottRepository;
	}

	@PostConstruct
	@Transactional
	public void generateData() {
		// Ott ott = ottRepository.findById(1L).get();
		//
		// List<User> users = generateMember();
		// userRepository.saveAll(users);
		//
		// List<Card> cards = generateCards(users);
		// cardRepository.saveAll(cards);

		List<User> users = userRepository.findAll();
		List<Party> parties = partyRepository.findAll();

		partyCapsuleRepository.saveAll(generatePartyCapsules(users, parties));
	}

	private List<PartyCapsule> generatePartyCapsules(List<User> users, List<Party> parties) {
		Deque<User> stack = new ArrayDeque<>(users);

		List<PartyCapsule> partyCapsules = new ArrayList<>();
		for (Party party : parties) {
			for (int i = 0; i < 4; i++) {
				User pop = stack.pop();
				PartyCapsule build = PartyCapsule.builder()
					.party(party)
					.expirationDate(LocalDate.of(2024, 6, 10))
					.cancelReservation(false)
					.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
					.partyMember(pop)
					.build();
				partyCapsules.add(build);
			}
		}
		return partyCapsules;
	}

	private List<Card> generateCards(List<User> users) {
		return users.stream()
			.map(user -> Card.buildNewCard(user, "23532532532", "1232132144"))
			.collect(Collectors.toList());
	}

	private List<Party> generateParty(List<User> users, Ott ott) {
		List<Party> parties = new ArrayList<>();
		for (int i = 0; i < 25000; i++) {
			User user = users.get(i);
			Party party = Party.builder()
				.ott(ott)
				.ottAccountId("testottAccountId" + 1)
				.ottAccountPassword("testottAccountPassword" + 1)
				.leader(user)
				.capacity(4)
				.build();

			parties.add(party);
		}
		return parties;
	}

	private List<User> generateMember() {
		List<User> users = new ArrayList<>();
		for (int i = 0; i < 200000; i++) {
			User user = User.builder()
				.nickName("테스트맨" + i)
				.telephone("01092053502")
				.password("dfsgdsg223123ggg")
				.email("jangu3384" + i + "@gmail.com")
				.role(Role.CLIENT)
				.build();
			users.add(user);
		}
		return users;
	}

}
