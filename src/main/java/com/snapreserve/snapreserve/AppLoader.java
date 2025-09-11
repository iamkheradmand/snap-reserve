package com.snapreserve.snapreserve;

import com.snapreserve.snapreserve.scheduler.SlotQueueRefresher;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppLoader implements ApplicationRunner {

	private final SlotQueueRefresher slotCacheRefresher;

	@Override
	public void run(ApplicationArguments args) {
		slotCacheRefresher.refresh();
	}

}
