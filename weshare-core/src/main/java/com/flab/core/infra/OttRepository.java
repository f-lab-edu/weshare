package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.Ott;

public interface OttRepository extends JpaRepository<Ott, Long> {
}
