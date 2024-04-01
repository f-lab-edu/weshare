package com.flab.weshare.domain.partyMatch.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.partyMatch.service.PartyMatchingService;
import com.flab.weshare.domain.partyMatch.service.util.TaskManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PartyMatchingScheduler {
	private final TaskManager<Long> taskManager = new TaskManager<>();

	@Autowired
	PartyMatchingService partyMatchingService;

	@Autowired
	OttRepository ottRepository;

	@Scheduled(cron = "0/10 * * * * *")
	//@Scheduled(cron = "0 0 0/1 * * *") //1시간 마다
	public void partyMatchingSchedule() {
		List<Ott> otts = ottRepository.findAll();
		for (Ott ott : otts) {
			try {
				runPartyMatchByOtt(ott);
			} catch (Exception e) {
				log.error("파티 매칭 비동기 메서드 호출시 에러", e);
			}
		}
	}

	private void runPartyMatchByOtt(Ott ott) {
		if (taskManager.addTask(ott.getId())) {
			partyMatchingService.partyMatch(ott)
				.thenAccept(taskManager::removeTask);
		}
	}
}
