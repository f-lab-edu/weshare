package com.flab.weshare.domain.pay.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query("select distinct pm from Payment pm "
		+ "join fetch pm.partyCapsule "
		+ "where pm.paymentStatus=:status")
	Page<Payment> findFetchPagePaymentByStatus(@Param("status") PaymentStatus status, Pageable pageable);
}
