package com.flab.weshare.domain.partyMatch.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.partyMatch.service.PartyMatchingService;

@Component
public class PartyMatchingScheduler {
	@Autowired
	PartyMatchingService partyMatchingService;

	@Autowired
	OttRepository ottRepository;

	@Scheduled(cron = "0/10 * * * * *")
	//@Scheduled(cron = "0 0 0/1 * * *") //1시간 마다
	public void partyMatchingSchedule() {
		List<Ott> otts = ottRepository.findAll();
		otts.forEach(partyMatchingService::partyMatch);
	}
}
