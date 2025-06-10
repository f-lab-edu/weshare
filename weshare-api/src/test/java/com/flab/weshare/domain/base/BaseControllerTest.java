package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
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
import com.flab.weshare.config.RabbitMQContainerConfig;
import com.flab.weshare.config.RedisTestContainerConfig;
import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.domain.auth.service.TokenManager;
import com.flab.weshare.utils.AesBytesEncryptUtil;
import com.flab.weshare.utils.jwt.JwtProperties;
import com.flab.weshare.utils.jwt.JwtUtil;

import jakarta.persistence.EntityManager;

@ActiveProfiles(value = "test")
@Transactional
@ExtendWith({RedisTestContainerConfig.class})
@Import({TestContainerConfig.class, RabbitMQContainerConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class BaseControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected TokenManager tokenManager;

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
	private AesBytesEncryptUtil aesBytesEncryptUtil;

	@Autowired
	EntityManager entityManager;

	@Autowired
	protected JwtUtil jwtUtil;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	protected String ACCESS_TOKEN;
	protected String REFRESH_TOKEN;

	protected OperationRequestPreprocessor preRequestProcessorWithAuthorization = preprocessRequest(
		modifyHeaders()
			.remove("Content-Length")
			.remove("X-Content-Type-Options")
			.remove("X-XSS-Protection")
			.remove("Cac"
				+ "he-Control")
			.remove("Pragma")
			.remove("Expires")
			.remove("X-Frame-Options"),
		prettyPrint());

	protected OperationRequestPreprocessor preRequestProcessor = preprocessRequest(
		modifyHeaders()
			.remove("Content-Length")
			.remove("X-Content-Type-Options")
			.remove("X-XSS-Protection")
			.remove("Cache-Control")
			.remove("Pragma")
			.remove("Expires")
			.remove("X-Frame-Options"),
		prettyPrint());

	protected OperationResponsePreprocessor preResponseProcessor = preprocessResponse(
		modifyHeaders()  // 헤더 내용 수정
			.remove("Content-Length")
			.remove("Host"),
		prettyPrint());

	@BeforeEach
	void setUpLogin() {
		userRepository.save(savedUser);
		ACCESS_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(savedUser.getId());
		REFRESH_TOKEN = JwtProperties.TOKEN_PREFIX + jwtUtil.createRefreshToken(savedUser.getId());
		tokenManager.saveToken(savedUser.getId(), REFRESH_TOKEN.replace(JwtProperties.TOKEN_PREFIX, ""));
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

		partyRepository.findAll()
			.forEach(
				party -> {
					party.changePassword(aesBytesEncryptUtil.encrypt(party.getOttAccountPassword()));
				}
			);

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
