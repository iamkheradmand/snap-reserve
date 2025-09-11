package com.snapreserve.snapreserve.service.manager;

import com.snapreserve.snapreserve.BaseIT;
import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import com.snapreserve.snapreserve.exception.NoAvailableSlotsException;
import com.snapreserve.snapreserve.service.manager.ReservationManagerService;
import com.snapreserve.snapreserve.service.manager.model.ReserveModel;
import com.snapreserve.snapreserve.service.manager.model.ReserveResultModel;
import com.snapreserve.snapreserve.service.publisher.ReservationEventPublisher;
import com.snapreserve.snapreserve.service.slotqueue.SlotQueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationManagerServiceImplIT extends BaseIT {

    @Autowired
    private ReservationManagerService reservationManagerService;

    @SpyBean
    private SlotQueueService slotService;

    @SpyBean
    private ReservationEventPublisher eventPublisher;

    @Test
    @DisplayName("""
            Given a reservation request
            When there are available slots
            Then the system should reserve a slot successfully and publish an event
            """)
    void doReserve_WithAvailableSlots_ShouldReserveSuccessfully() {
        String userName = "testUser";
        Long slotId = 1001L;
        String slotValue = slotId + "|09:00|10:00";
        ReserveModel request = new ReserveModel(userName);

        String expectedReservationId = userName + ":" + slotId;
        String expectedDisplayRange = "09:00 to 10:00";

        doReturn(slotValue).when(slotService)
                .popAndHoldSlot();

        ReserveResultModel result = reservationManagerService.doReserve(request);

        assertThat(result).isNotNull();
        assertThat(result.reservationId()).isEqualTo(expectedReservationId);
        assertThat(result.reservationDate()).isEqualTo(expectedDisplayRange);
        verify(eventPublisher, times(1)).publish(any(ReservationEvent.class));
        verify(slotService, times(1)).popAndHoldSlot();
    }

    @Test
    @DisplayName("""
            Given a reservation request
            When there are no available slots
            Then the system should throw NoAvailableSlotsException
            """)
    void doReserve_WithNoAvailableSlots_ShouldThrowException() {
        String userName = "testUser";
        ReserveModel request = new ReserveModel(userName);

        when(slotService.popAndHoldSlot()).thenReturn(null);

        assertThatThrownBy(() -> reservationManagerService.doReserve(request))
                .isInstanceOf(NoAvailableSlotsException.class)
                .hasMessage("No available Reservation slots");

        verify(slotService, times(1)).popAndHoldSlot();
        verify(eventPublisher, never()).publish(any(ReservationEvent.class));
    }

}
