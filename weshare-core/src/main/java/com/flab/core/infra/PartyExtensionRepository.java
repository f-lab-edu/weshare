package com.flab.core.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.core.entity.PartyExtension;

public interface PartyExtensionRepository extends JpaRepository<PartyExtension, Long> {
}
