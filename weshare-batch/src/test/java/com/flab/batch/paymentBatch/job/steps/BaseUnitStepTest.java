package com.flab.batch.paymentBatch.job.steps;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.flab.batch.localBatchDBHelper.BatchDataGenerator;
import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.core.entity.EmailSendResult;
import com.flab.core.entity.Money;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.PartyExtension;
import com.flab.core.entity.PayResultStatus;
import com.flab.core.entity.Payment;
import com.flab.core.entity.PaymentResult;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartyExtensionRepository;
import com.flab.core.infra.PayResultRepository;
import com.flab.core.infra.PaymentRepository;
import com.flab.core.infra.UserRepository;

import lombok.extern.slf4j.Slf4j;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public abstract class BaseUnitStepTest {
	protected final String PAY_DATE_STRING = "2024-09-01";
	protected final String RENEW_DATE_STRING = "2024-10-01";
	protected final String TARGET_PARTY_CAPSULE_STATUS_STRING = "OCCUPIED";
	protected final LocalDate PAY_DATE = LocalDate.of(2024, 9, 1);
	protected final LocalDate RENEW_DATE = LocalDate.of(2024, 10, 1);
	protected final PartyCapsuleStatus TARGET_PARTY_CAPSULE_STATUS = PartyCapsuleStatus.OCCUPIED;

	@Autowired
	protected PaymentRepository paymentRepository;
	@Autowired
	protected PartyExtensionRepository partyExtensionRepository;
	@Autowired
	protected CardRepository cardRepository;
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected PartyCapsuleRepository partyCapsuleRepository;
	@Autowired
	protected BatchDataGenerator batchDataGenerator;
	@Autowired
	protected PayResultRepository payResultRepository;

	@Autowired
	protected JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	protected JobRepositoryTestUtils jobRepositoryTestUtils;

	@MockBean
	protected PayJobParameter payJobParameter;

	protected final JobParameters jobParameters = new JobParametersBuilder()
		.addString("payDate", PAY_DATE_STRING)
		.addString("renewExpirationDate", RENEW_DATE_STRING)
		.addString("targetPartyCapsuleStatus", TARGET_PARTY_CAPSULE_STATUS_STRING)
		.toJobParameters();

	@BeforeAll
	void generateDatas() {
		batchDataGenerator.generateData(10);
	}

	@BeforeEach
	void parameterMocking() {
		when(payJobParameter.getPayJobDate()).thenReturn(PAY_DATE);
		when(payJobParameter.getRenewExpirationDate()).thenReturn(RENEW_DATE);
		when(payJobParameter.getStatus()).thenReturn(TARGET_PARTY_CAPSULE_STATUS);
	}

	@AfterEach
	public void removeBatchData() {
		partyExtensionRepository.deleteAll();
		payResultRepository.deleteAll();
		paymentRepository.deleteAll();
	}

	protected List<Payment> createPayments(List<PartyCapsule> partyCapsules) {
		List<Payment> payments = new ArrayList<>();
		partyCapsules.forEach(partyCapsule -> {
			Payment payment = Payment.builder()
				.partyCapsule(partyCapsule)
				.payDate(
					LocalDate.parse((String)Objects.requireNonNull(jobParameters.getParameter("payDate")).getValue()))
				.card(cardRepository.getReferenceById(1L))
				.paymentResult(PaymentResult.builder().payResultStatus(PayResultStatus.SUCCESS).build())
				.amount(new Money(1000))
				.build();
			payments.add(payment);
		});
		paymentRepository.saveAll(payments);
		return payments;
	}

	protected List<PartyExtension> createPartyExtensions(List<Payment> payments) {
		List<PartyExtension> partyExtensions = new ArrayList<>();
		payments.forEach(payment -> {
			PartyExtension partyExtension = PartyExtension.builder()
				.payment(payment)
				.renewExpirationDate(
					LocalDate.parse((String)jobParameters.getParameter("renewExpirationDate").getValue()))
				.emailSendResult(EmailSendResult.INITIAL)
				.partyCapsule(payment.getPartyCapsule())
				.build();
			partyExtensions.add(partyExtension);
		});
		partyExtensionRepository.saveAll(partyExtensions);
		return partyExtensions;
	}

	@AfterEach
	void clearBatchRepository() {
		jobRepositoryTestUtils.removeJobExecutions();
	}
}
