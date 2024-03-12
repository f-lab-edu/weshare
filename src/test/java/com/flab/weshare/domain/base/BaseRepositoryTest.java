package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.entity.PartyMember;
import com.flab.weshare.domain.party.entity.PartyMemberStatus;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyMemberRepository;
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
	protected PartyMemberRepository partyMemberRepository;

	@Autowired
	protected OttRepository ottRepository;

	protected Party savedParty;

	@BeforeEach
	void setUp() {
		List<User> members = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			User build = User.builder()
				.nickName(NICKNAME + i)
				.email(EMAIL)
				.password(PASSWORD)
				.telephone(TELEPHONE)
				.role(Role.CLIENT)
				.build();

			members.add(build);
		}

		userRepository.save(savedUser);
		userRepository.saveAll(members);

		Ott testOtt = Ott.builder()
			.name("test")
			.leaderFee(3000)
			.commonFee(2000)
			.maximumCapacity(4)
			.build();

		ottRepository.save(testOtt);

		savedParty = Party.builder()
			.leader(savedUser)
			.ott(testOtt)
			.capacity(4)
			.ottAccountId("dsafadsfd")
			.ottAccountPassword("adsfeeff33")
			.build();

		partyRepository.save(savedParty);

		for (int i = 0; i < 3; i++) {
			PartyMember build = PartyMember.builder()
				.party(savedParty)
				.partyMember(members.get(i))
				.partyMemberStatus(PartyMemberStatus.ATTENDING)
				.build();
			partyMemberRepository.save(build);
		}
	}

}
