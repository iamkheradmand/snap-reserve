package com.snapreserve.snapreserve.service.reservationrequest;

import com.snapreserve.snapreserve.model.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.eventpublisher.ReservationEventPublisher;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveModel;
import com.snapreserve.snapreserve.service.reservationrequest.model.ReserveResultModel;
import com.snapreserve.snapreserve.service.slotqueue.SlotQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationRequestServiceImpl implements ReservationRequestService {

    private final SlotQueueService slotService;

    private final ReservationEventPublisher eventPublisher;

    @Override
    public ReserveResultModel doReserve(ReserveModel model) {
        String slot = slotService.popAndHoldSlot();
        if (slot == null) {
            throw new RuntimeException("No available slots");
        }
        Long slotId = Long.valueOf(slot.split(":")[0]);
        String slotDate = slot.split(":")[1];

        ReservationEvent event = new ReservationEvent();
        event.setUserName(model.userName());
        event.setSlotId(slotId);

        eventPublisher.publish(event);

        return new ReserveResultModel(slotDate);
    }

}
