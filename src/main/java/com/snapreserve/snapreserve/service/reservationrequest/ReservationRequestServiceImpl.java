package com.snapreserve.snapreserve.service.reservationrequest;

import com.snapreserve.snapreserve.exception.NoAvailableSlotsException;
import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import com.snapreserve.snapreserve.service.eventpublisher.ReservationEventPublisher;
import com.snapreserve.snapreserve.service.reservationrequest.model.ParsedSlot;
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
            throw new NoAvailableSlotsException("No available Reservation slots");
        }
        ParsedSlot parsedSlot = ParsedSlot.parse(slot);
        String reservationId = model.userName() + ":" + parsedSlot.id();

        ReservationEvent event = new ReservationEvent();
        event.setReservationId(reservationId);
        event.setUserName(model.userName());
        event.setSlotId(parsedSlot.id());
        eventPublisher.publish(event);

        log.info("Slot {} reserved successfully for user {} with reservationId {}", parsedSlot.id(), model.userName(), reservationId);
        return new ReserveResultModel(reservationId, parsedSlot.getDisplayRange());
    }

}
