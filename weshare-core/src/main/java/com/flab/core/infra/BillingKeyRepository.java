package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.BillingKey;
import com.flab.core.entity.BillingKeyStatus;
import com.flab.core.entity.User;

public interface BillingKeyRepository extends JpaRepository<BillingKey, Long> {
	BillingKey findByUser(User user);

	BillingKey findByUserAndBillingKeyStatus(User user, BillingKeyStatus billingKeyStatus);
}
