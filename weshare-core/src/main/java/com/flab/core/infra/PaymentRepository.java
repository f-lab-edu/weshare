package com.flab.core.infra;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flab.core.entity.Payment;
import com.flab.core.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query(value = "select distinct pm from Payment pm "
		+ "join fetch pm.partyCapsule "
		+ "join fetch pm.card "
		+ "left outer join fetch pm.payResult "
		, countQuery = "select count(pm) from Payment pm ")
	Page<Payment> findFetchPagePaymentByStatus(@Param("status") PaymentStatus status, Pageable pageable);

	@Query(value = "select distinct pm from Payment pm "
		+ "join fetch pm.partyCapsule "
		+ "join fetch pm.payResult "
		+ "where pm.payDate=:payment_date "
		, countQuery = "select count(pm) from Payment pm where pm.payDate=:payment_date")
	Page<Payment> findFetchPagePaymentByPayDate(@Param("payment_date") LocalDate payment_date, Pageable pageable);
}
