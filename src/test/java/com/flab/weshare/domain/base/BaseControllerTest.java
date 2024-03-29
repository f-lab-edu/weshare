package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.party.entity.PartyCapsuleStatus;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyCapsuleRepository;
import com.flab.weshare.domain.party.repository.PartyRepository;
import com.flab.weshare.domain.user.entity.Role;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.utils.jwt.JwtProperties;
import com.flab.weshare.utils.jwt.JwtUtil;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@Transactional
@Import({TestContainerConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected OttRepository ottRepository;

	@Autowired
	protected PartyRepository partyRepository;

	@Autowired
	protected PartyCapsuleRepository partyCapsuleRepository;

	@Autowired
	EntityManager entityManager;

	@Autowired
	JwtUtil jwtUtil;

	protected String ACCESS_TOKEN;
	protected String REFRESH_TOKEN;

	@BeforeEach
	void setUpLogin() {
		userRepository.save(savedUser);
		ACCESS_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(savedUser.getId());
		REFRESH_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createRefreshToken(savedUser.getId());
	}

	@BeforeEach
	void saveEntities() {
		ottRepository.save(savedOtt);
		partyRepository.save(savedParty);

		List<User> users = createUsers();
		userRepository.saveAll(users);

		List<PartyCapsule> partyCapsules = createPartyCapsules(users);
		partyCapsuleRepository.saveAll(partyCapsules);

		entityManager.clear();
	}

	private List<PartyCapsule> createPartyCapsules(List<User> users) {
		List<PartyCapsule> partyCapsules = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			PartyCapsule partyCapsule = PartyCapsule.builder()
				.party(savedParty)
				.partyMember(users.get(i))
				.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
				.build();
			partyCapsules.add(partyCapsule);
		}
		return partyCapsules;
	}

	private List<User> createUsers() {
		List<User> members = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			User build = User.builder()
				.nickName(NICKNAME + i)
				.email(EMAIL + i)
				.password(PASSWORD)
				.telephone(TELEPHONE)
				.role(Role.CLIENT)
				.build();
			members.add(build);
		}
		return members;
	}
}
