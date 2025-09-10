package com.snapreserve.snapreserve.service.slotqueue;

import java.util.List;

import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;

public interface SlotQueueService {
	String popAndHoldSlot();

	void refreshAvailableSlots(List<AvailableSlots> slots);

}
