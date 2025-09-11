package com.snapreserve.snapreserve.service.reservation;

import com.snapreserve.snapreserve.BaseIT;
import com.snapreserve.snapreserve.exception.ReservationSlotException;
import com.snapreserve.snapreserve.repository.reservation.Reservation;
import com.snapreserve.snapreserve.repository.reservation.ReservationRepository;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import com.snapreserve.snapreserve.service.reservation.model.DeleteReserveModel;
import com.snapreserve.snapreserve.service.reservation.model.PersistReserveModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ReservationServiceImplIT extends BaseIT {

    @Autowired
    private ReservationServiceImpl reservationService;

    @Autowired
    private AvailableSlotsRepository slotsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }


    @Test
    @Transactional
    @DisplayName("""
            Given valid user and slot exist
            When persistReservation is called
            Then reservation should be created successfully
            """)
    void persistReservation_WithValidData_ShouldCreateReservation() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String expectedUsername = "testUser" + uuid;
        String expectedEmail = "test_" + uuid + "@example.com";
        int expectedListSize = 1;

        Users user = createUser(expectedUsername, expectedEmail);
        user = userRepository.save(user);

        AvailableSlots slot = createAvailableSlot(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        slot = slotsRepository.save(slot);

        long expectedSlotId = slot.getId();
        String expectedReservationId = expectedUsername + ":" + expectedSlotId;

        PersistReserveModel model = new PersistReserveModel(expectedReservationId, expectedUsername, expectedSlotId);
        reservationService.persistReservation(model);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(expectedListSize);

        Reservation reservation = reservations.get(0);
        assertThat(reservation.getReservationId()).isEqualTo(expectedReservationId);
        assertThat(reservation.getUser().getUsername()).isEqualTo(expectedUsername);
        assertThat(reservation.getSlot().getId()).isEqualTo(expectedSlotId);
        assertThat(reservation.getSlot().is_reserved()).isTrue();

        AvailableSlots updatedSlot = slotsRepository.findById(slot.getId()).orElseThrow();
        assertThat(updatedSlot.is_reserved()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("""
            Given slot does not exist
            When persistReservation is called
            Then should throw ReservationSlotException
            """)
    void persistReservation_WithNonExistentSlot_ShouldThrowException() {
        String username = "testUser";
        long slotId = 1L;
        String reservationId = username + ":" + slotId;

        Users user = createUser(username, "test@example.com");
        userRepository.save(user);

        PersistReserveModel model = new PersistReserveModel(reservationId, username, slotId);

        assertThatThrownBy(() -> reservationService.persistReservation(model))
                .isInstanceOf(ReservationSlotException.class)
                .hasMessage("Slot not found:" + slotId);

        assertThat(reservationRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("""
            Given slot is already reserved
            When persistReservation is called
            Then should throw ReservationSlotException
            """)
    void persistReservation_WithAlreadyReservedSlot_ShouldThrowException() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String username = "testUser" + uuid;
        String email = "test_" + uuid + "@example.com";

        Users user = createUser(username, email);
        userRepository.save(user);

        AvailableSlots slot = createAvailableSlot(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        slot.set_reserved(true);
        slotsRepository.save(slot);

        long slotId = slot.getId();
        String reservationId = username + ":" + slotId;

        PersistReserveModel model = new PersistReserveModel(reservationId, username, slotId);

        assertThatThrownBy(() -> reservationService.persistReservation(model))
                .isInstanceOf(ReservationSlotException.class)
                .hasMessage("Slot already reserved");

        assertThat(reservationRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("""
            Given existing reservation
            When deleteReservation is called
            Then reservation should be deleted
            """)
    void deleteReservation_WithExistingReservation_ShouldDeleteReservation() {
        String expectedUsername = "testUser";
        long expectedSlotId = 1L;
        String expectedReservationId = expectedUsername + ":" + expectedSlotId;

        Users user = createUser(expectedUsername, "test@example.com");
        user = userRepository.save(user);

        AvailableSlots slot = createAvailableSlot(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        slot = slotsRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setReservationId(expectedReservationId);
        reservation.setUser(user);
        reservation.setSlot(slot);
        reservationRepository.save(reservation);

        DeleteReserveModel model = new DeleteReserveModel(expectedReservationId);

        reservationService.deleteReservation(model);

        assertThat(reservationRepository.findAll()).isEmpty();
        assertThat(reservationRepository.findByReservationId(expectedReservationId).isEmpty()).isTrue();
    }

    void cleanDatabase() {
        reservationRepository.deleteAll();
        slotsRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Users createUser(String username, String email) {
        Users user = new Users();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }

    private AvailableSlots createAvailableSlot(LocalDateTime startTime, LocalDateTime endTime) {
        AvailableSlots slot = new AvailableSlots();
        slot.setStart_time(startTime);
        slot.setEnd_time(endTime);
        slot.set_reserved(false);
        return slot;
    }
}
