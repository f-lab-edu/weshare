package com.flab.weshare.domain.partyMatch.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.flab.weshare.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmptyCapsulePublisher {
	private final RabbitTemplate rabbitTemplate;

	public void publishPartySlotAvailable(PartyJoinMessage message) {
		try {
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
		} catch (Exception e) {
			log.error("message publish error : {}", e.getMessage());
		}
	}
}
