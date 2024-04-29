package com.flab.weshare.domain.pay.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query(value = "select distinct pm from Payment pm "
		+ "join fetch pm.partyCapsule "
		+ "left outer join fetch pm.payResult "
		+ "where pm.payResult is null"
		, countQuery = "select count(pm) from Payment pm where pm.payResult is null")
	Page<Payment> findFetchPagePaymentByStatus(@Param("status") PaymentStatus status, Pageable pageable);

	@Query(value = "select distinct pm from Payment pm "
		+ "join fetch pm.partyCapsule "
		+ "join fetch pm.payResult "
		+ "where pm.payDate=:payment_date "
		, countQuery = "select count(pm) from Payment pm where pm.payDate=:payment_date")
	Page<Payment> findFetchPagePaymentByPayDate(@Param("payment_date") LocalDate payment_date, Pageable pageable);
}
