package com.snapreserve.snapreserve.scheduler;

import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import com.snapreserve.snapreserve.service.slotqueue.SlotQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlotQueueRefresher {

	private final AvailableSlotsRepository slotRepository;
	private final SlotQueueService slotCacheService;

	@Scheduled(fixedDelayString = "${scheduler.slot-queue.update.interval}")
	@SchedulerLock(name = "refreshAvailableSlots", lockAtMostFor = "PT7M", lockAtLeastFor = "PT5M")
	public void refresh() {
		PageRequest pageRequest = PageRequest.of(0, 10_000);
		Slice<AvailableSlots> availableSlots = slotRepository.findAvailableSlots(pageRequest);
		slotCacheService.refreshAvailableSlots(availableSlots.getContent());
	}

}
