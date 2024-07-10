package com.flab.batch.localBatchDBHelper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.flab.core.entity.Card;
import com.flab.core.entity.Ott;
import com.flab.core.entity.Party;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.Role;
import com.flab.core.entity.User;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.PartyRepository;
import com.flab.core.infra.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("data-generate")
public class BatchDataGenerator {
	private static final int BATCH_SIZE = 10000;

	private final UserRepository userRepository;
	private final PartyRepository partyRepository;
	private final CardRepository cardRepository;
	private final JdbcTemplate jdbcTemplate;
	private final Ott[] ott = new Ott[8];

	public BatchDataGenerator(UserRepository userRepository, PartyRepository partyRepository,
		CardRepository cardRepository,
		DataSource dataSource) {
		this.userRepository = userRepository;
		this.partyRepository = partyRepository;
		this.cardRepository = cardRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		for (int i = 1; i <= 7; i++) {
			ott[i] = Ott.builder().id((long)i).build();
		}
	}

	public void generateData() {
		long plusCount = 500000; // 추가하고자하는 파티캡슐의 갯수.

		List<Card> cards = generateCards(plusCount);
		Page<Card> savedCard = cardRepository.findAll(
			PageRequest.of(0, cards.size(), Sort.by("id").descending()));

		List<User> users = generateMember(plusCount, savedCard.getContent());
		Page<User> savedUsers = userRepository.findAll(
			PageRequest.of(0, users.size(), Sort.by("id").descending()));

		List<Party> parties = generateParty(savedUsers.getContent());
		Page<Party> savedParty = partyRepository.findAll(
			PageRequest.of(0, parties.size(), Sort.by("id").descending()));
		generatePartyCapsules(savedUsers.getContent(), savedParty.getContent());
	}

	private List<PartyCapsule> generatePartyCapsules(List<User> users, List<Party> parties) {
		Deque<User> dq = new ArrayDeque<>(users);
		List<PartyCapsule> partyCapsules = new ArrayList<>();
		for (Party party : parties) {
			int cnt = 0;
			dq.pollFirst();
			while (!dq.isEmpty() && cnt++ <= 4) {
				User pop = dq.pollFirst();
				PartyCapsule build = PartyCapsule.builder()
					.party(party)
					.expirationDate(LocalDate.of(2024, 7, 15))
					.cancelReservation(false)
					.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
					.partyMember(pop)
					.ott(party.getOtt())
					.build();
				partyCapsules.add(build);
			}
		}

		String sql =
			"INSERT INTO weshare.party_capsule (created_date, modified_date, party_capsule_status, party_id, user_id,expiration_date, cancel_reservation, join_date, ott_id) "
				+ "VALUES ('2024-07-20 15:59:04.000000', '2024-07-20 15:59:04.000000', ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, partyCapsules, BATCH_SIZE, (ps, pc) -> {
			ps.setString(1, pc.getPartyCapsuleStatus().name());
			ps.setLong(2, pc.getParty().getId());
			ps.setLong(3, pc.getPartyMember().getId());
			ps.setDate(4, Date.valueOf("2024-07-10"));
			ps.setBoolean(5, false);
			ps.setDate(6, Date.valueOf("2024-07-10"));
			ps.setLong(7, pc.getOtt().getId());
		});

		return partyCapsules;
	}

	private List<Card> generateCards(long plusCount) {
		List<Card> cards = new ArrayList<>();
		long total = plusCount + (long)Math.ceil(plusCount / 4.0);
		for (int i = 0; i < total; i++) {
			Card newCard = Card.buildNewCard("23532532532", "1232132144");
			cards.add(newCard);
		}

		String sql =
			"INSERT INTO weshare.card (card_number, billing_key, card_status, created_date, modified_date) "
				+ "VALUES (?, ?, ?, '2024-07-20 15:59:04.000000', '2024-07-20 15:59:04.000000')";

		jdbcTemplate.batchUpdate(sql, cards, BATCH_SIZE, (ps, card) -> {
			ps.setString(1, card.getCardNumber());
			ps.setString(2, card.getBillingKey());
			ps.setString(3, card.getCardStatus().name());
		});

		return cards;
	}

	private List<Party> generateParty(List<User> users) {
		List<Party> parties = new ArrayList<>();
		int ottRotation = 0;
		for (int i = 0; i < users.size(); i += 5) {
			User user = users.get(i);
			Party party = Party.builder()
				.ott(ott[++ottRotation % 7 + 1])
				.ottAccountId("testottAccountId" + user.getId())
				.ottAccountPassword("testottAccountPassword" + user.getId())
				.leader(user)
				.capacity(4)
				.build();
			parties.add(party);
		}

		String sql =
			"INSERT INTO weshare.party (created_date, modified_date, capacity, ott_account_id, ott_account_password, party_status, user_id, ott_id)"
				+ "VALUES ('2024-07-20 15:59:04.000000', '2024-07-20 15:59:04.000000', ?, ?, ?, ?, ?,?)";

		jdbcTemplate.batchUpdate(sql, parties, BATCH_SIZE, (ps, party) -> {
			ps.setInt(1, 4);
			ps.setString(2, party.getOttAccountId());
			ps.setString(3, party.getOttAccountPassword());
			ps.setString(4, party.getPartyStatus().name());
			ps.setLong(5, party.getLeader().getId());
			ps.setLong(6, party.getOtt().getId());
		});

		return parties;
	}

	private List<User> generateMember(long plusCount, List<Card> cards) {
		List<User> users = new ArrayList<>();
		long total = plusCount + (long)Math.ceil(plusCount / 4.0);
		for (int i = 0; i < total; i++) {
			User user = User.builder()
				.nickName("테스트맨" + LocalDateTime.now() + i)
				.telephone("01092053502")
				.password("dfsgdsg223123ggg" + plusCount + i)
				.email("jangu3384" + i + "@gmail.com")
				.availableCard(cards.get(i))
				.role(Role.CLIENT)
				.build();
			users.add(user);
		}

		String sql =
			"INSERT INTO weshare.users (created_date, modified_date, email, nick_name, password, role, telephone) "
				+ "VALUES ('2024-07-20 15:59:04.000000', '2024-07-20 15:59:04.000000', ?,?,?,?,?)";

		jdbcTemplate.batchUpdate(sql, users, BATCH_SIZE, (ps, user) -> {
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getNickName());
			ps.setString(3, user.getPassword());
			ps.setString(4, user.getRole().name());
			ps.setString(5, user.getTelephone());
		});
		return users;
	}

}
