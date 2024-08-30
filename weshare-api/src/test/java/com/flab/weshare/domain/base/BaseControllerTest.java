package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.Role;
import com.flab.core.entity.User;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartyRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.config.RedisTestContainerConfig;
import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.utils.jwt.JwtProperties;
import com.flab.weshare.utils.jwt.JwtUtil;

import jakarta.persistence.EntityManager;

@ActiveProfiles(value = "test")
@Transactional
@ExtendWith({RedisTestContainerConfig.class})
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
	protected JwtUtil jwtUtil;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	protected String ACCESS_TOKEN;
	protected String REFRESH_TOKEN;

	@BeforeEach
	void setUpLogin() {
		userRepository.save(savedUser);
		ACCESS_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(savedUser.getId());
		REFRESH_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createRefreshToken(savedUser.getId());
		redisTemplate.opsForValue().set(
			String.valueOf(savedUser.getId()),
			REFRESH_TOKEN.replace(JwtProperties.TOKEN_PREFIX, ""),
			10000000,
			TimeUnit.MILLISECONDS
		);
	}

	@BeforeEach
	void saveEntities() {
		ottRepository.save(savedOtt);
		partyRepository.save(savedParty);

		List<User> users = createUsers();
		userRepository.saveAll(users);

		PartyCapsule partyCapsule = PartyCapsule.builder()
			.party(partyRepository.getReferenceById(savedParty.getId()))
			.partyMember(savedUser)
			.ott(savedParty.getOtt())
			.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
			.joinDate(LocalDate.now())
			.build();
		partyCapsuleRepository.save(partyCapsule);
		List<PartyCapsule> partyCapsules = createPartyCapsules(users);
		partyCapsuleRepository.saveAll(partyCapsules);

		entityManager.flush();
		entityManager.clear();
	}

	private List<PartyCapsule> createPartyCapsules(List<User> users) {
		List<PartyCapsule> partyCapsules = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			PartyCapsule partyCapsule = PartyCapsule.builder()
				.party(savedParty)
				.partyMember(users.get(i))
				.ott(savedParty.getOtt())
				.partyCapsuleStatus(PartyCapsuleStatus.OCCUPIED)
				.joinDate(LocalDate.now())
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
