package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.PayRes;

public interface PayResRepository extends JpaRepository<PayRes, Long> {
}
