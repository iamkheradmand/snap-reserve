package com.snapreserve.snapreserve.service.publisher;

import com.snapreserve.snapreserve.BaseIT;
import com.snapreserve.snapreserve.dto.msg.ReservationEvent;
import com.snapreserve.snapreserve.exception.ReservationDefaultException;
import com.snapreserve.snapreserve.service.reservation.ReservationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class ReservationEventPublisherImplIT extends BaseIT {

    @Autowired
    private ReservationEventPublisher reservationEventPublisher;

    @SpyBean
    private ReservationService reservationService;

    @Test
    @DisplayName("""
            Given a valid reservation event
            When publish is called
            Then event should be sent to RabbitMQ successfully
            """)
    void publish_WithValidEvent_ShouldSendToRabbitMQ() {
        ReservationEvent event = createReservationEvent("testUser", 123L, "testUser:123");

        reservationEventPublisher.publish(event);

        assertThatNoException().isThrownBy(() -> reservationEventPublisher.publish(event));
    }

    @Test
    @Disabled
    @DisplayName("""
            Given RabbitMQ is unavailable
            When publish is called
            Then should throw ReservationDefaultException
            """)
    void publish_WhenRabbitMQUnavailable_ShouldThrowException() {
        rabbitMQContainer.stop();

        ReservationEvent event = createReservationEvent("testUser", 124L, "testUser:124");

        assertThatThrownBy(() -> reservationEventPublisher.publish(event))
                .isInstanceOf(ReservationDefaultException.class)
                .hasMessage("rabbitmq Failed - an error happen, please try again");

        rabbitMQContainer.start();
    }

    @Test
    @DisplayName("""
            Given a reservation event is published
            When listener receives the message
            Then should call reservation service to persist
            """)
    void listener_WhenMessageReceived_ShouldPersistReservation() throws InterruptedException {
        ReservationEvent event = createReservationEvent("testUser6", 126L, "testUser:126");

        reservationEventPublisher.publish(event);

        org.awaitility.Awaitility.await().atMost(Duration.ofSeconds(5)).await().untilAsserted(() -> {
            verify(reservationService, times(1))
                    .persistReservation(any());
        });
    }

    private ReservationEvent createReservationEvent(String userName, Long slotId, String reservationId) {
        ReservationEvent event = new ReservationEvent();
        event.setUserName(userName);
        event.setSlotId(slotId);
        event.setReservationId(reservationId);
        return event;
    }

}

