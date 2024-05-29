package com.flab.weshare.domain.pay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.flab.weshare.domain.pay.service.BootPayPaymentProcessor;

@SpringBootTest(classes = {BootPayPaymentProcessor.class})
class BootPayPaymentProcessorTest {
	@Autowired
	BootPayPaymentProcessor bootPayPaymentProcessor;

	@Test
	void test1() {
		// String billingKey = "660d135be57a7e003d5a9220";
		// Integer amount = 100;
		// bootPayPaymentProcessor.requestPayment(billingKey, amount, "테스트1");
		// bootPayPaymentProcessor.requestPayment(billingKey, amount, "테스트2");
		// bootPayPaymentProcessor.requestPayment(billingKey, amount, "테스트3");
		// bootPayPaymentProcessor.requestPayment(billingKey, amount, "테스트4");
		// bootPayPaymentProcessor.requestPayment(billingKey, amount, "테스트3");

	}
}
