package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.PayResult;

public interface PayResultRepository extends JpaRepository<PayResult, Long> {
}
