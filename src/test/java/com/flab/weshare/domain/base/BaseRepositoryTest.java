package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.party.entity.PartyCapsuleStatus;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyCapsuleRepository;
import com.flab.weshare.domain.party.repository.PartyRepository;
import com.flab.weshare.domain.user.entity.Role;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;

@ActiveProfiles("test")
@Import({TestContainerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public abstract class BaseRepositoryTest {
	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected PartyRepository partyRepository;

	@Autowired
	protected PartyCapsuleRepository partyCapsuleRepository;

	@Autowired
	protected OttRepository ottRepository;

	protected Party savedParty;

	@BeforeEach
	void setUp() {
		List<User> members = generateMembers();
		userRepository.save(savedUser);
		userRepository.saveAll(members);

		Ott testOtt = generteOtt();
		ottRepository.save(testOtt);
		savedParty = generteParty(testOtt);
		partyRepository.save(savedParty);

		List<PartyCapsule> partyCapsules = preparePartyCapsules(members);
		partyCapsuleRepository.saveAll(partyCapsules);
	}

	@NotNull
	private List<PartyCapsule> preparePartyCapsules(List<User> members) {
		List<PartyCapsule> partyCapsules = new ArrayList<>();
		addOccupied(members, partyCapsules);
		partyCapsules.add(PartyCapsule.makeEmptyCapsule(savedParty));
		addWithdrawns(members, partyCapsules);
		return partyCapsules;
	}

	private void addWithdrawns(List<User> members, List<PartyCapsule> partyCapsules) {
		for (int i = 10; i < 15; i++) {
			PartyCapsule build = PartyCapsule.builder()
				.party(savedParty)
				.partyMember(members.get(i))
				.partyCapsuleStatus(PartyCapsuleStatus.WITHDRAWN)
				.build();
			partyCapsules.add(build);
		}
	}

	private void addOccupied(List<User> members, List<PartyCapsule> partyCapsules) {
		for (int i = 0; i < 2; i++) {
			PartyCapsule build = PartyCapsule.builder()
				.party(savedParty)
				.partyMember(members.get(i))
				.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
				.build();
			partyCapsules.add(build);
		}
	}

	private Party generteParty(Ott testOtt) {
		return Party.builder()
			.leader(savedUser)
			.ott(testOtt)
			.capacity(4)
			.ottAccountId("dsafadsfd")
			.ottAccountPassword("adsfeeff33")
			.build();
	}

	private Ott generteOtt() {
		Ott testOtt = Ott.builder()
			.name("test")
			.leaderFee(3000)
			.commonFee(2000)
			.maximumCapacity(3)
			.build();
		return testOtt;
	}

	@NotNull
	private List<User> generateMembers() {
		List<User> members = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			User build = User.builder()
				.nickName(NICKNAME + i)
				.email(EMAIL)
				.password(PASSWORD)
				.telephone(TELEPHONE)
				.role(Role.CLIENT)
				.build();

			members.add(build);
		}
		return members;
	}

}
