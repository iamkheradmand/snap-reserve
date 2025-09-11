package com.snapreserve.snapreserve.controller;

import com.snapreserve.snapreserve.BaseIT;
import com.snapreserve.snapreserve.dto.reponse.ReservationResponse;
import com.snapreserve.snapreserve.dto.request.ReservationRequest;
import com.snapreserve.snapreserve.repository.reservation.Reservation;
import com.snapreserve.snapreserve.repository.reservation.ReservationRepository;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.sluts.AvailableSlotsRepository;
import com.snapreserve.snapreserve.repository.user.UserRepository;
import com.snapreserve.snapreserve.repository.user.Users;
import com.snapreserve.snapreserve.scheduler.SlotQueueRefresher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationControllerIT extends BaseIT {

    @LocalServerPort
    protected String port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private AvailableSlotsRepository slotsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SlotQueueRefresher queueRefresher;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    public String getBaseUrl() {
        return String.format("http://localhost:%s/api/v1/reservation", port);
    }

    @Test
    @DisplayName("""
            Given a valid reservation request
            When POST /api/reservation/v1/ is called
            Then should return successful reservation response and reservation should be created successfully
            """)
    void reserve_WithValidRequest_ShouldReturnSuccess() throws Exception {
        String expectedUsername = "iamkheradmand";
        executeSqlScript("sql/test-data.sql");
        queueRefresher.refresh();

        ReservationRequest request = new ReservationRequest();
        request.setUserName(expectedUsername);
        ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()),
                ReservationResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String reservationId = response.getBody().getReservationId();
        assertThat(reservationId).isNotBlank();

        int expectedListSize = 1;
        long expectedSlotId = Long.parseLong(reservationId.split(":")[1]);
        // waiting for persistence
        org.awaitility.Awaitility.await().atMost(Duration.ofSeconds(5)).await().untilAsserted(() -> {
            List<Reservation> reservations = reservationRepository.findAll();
            assertThat(reservations).hasSize(expectedListSize);

            Reservation reservation = reservations.get(0);
            assertThat(reservation.getReservationId()).isEqualTo(reservationId);
            assertThat(reservation.getUser().getUsername()).isEqualTo(expectedUsername);
            assertThat(reservation.getSlot().is_reserved()).isTrue();

            AvailableSlots updatedSlot = slotsRepository.findById(expectedSlotId).orElseThrow();
            assertThat(updatedSlot.is_reserved()).isTrue();
        });
    }

    @Test
    @DisplayName("""
            Given multiple concurrent reservation requests with only 2 available slots
            When POST /api/reservation/v1/ is called
            Then should handle all requests
            """)
    void reserve_ConcurrentRequests_ShouldHandleAll() throws Exception {
        executeSqlScript("sql/test-concurrency-2-slots.sql");
        queueRefresher.refresh();

        ReservationRequest request1 = new ReservationRequest();
        request1.setUserName("iamkheradmand");
        ReservationRequest request2 = new ReservationRequest();
        request2.setUserName("iamryan");
        ReservationRequest request3 = new ReservationRequest();
        request3.setUserName("charlie_brown");
        ReservationRequest request4 = new ReservationRequest();
        request4.setUserName("diana_prince");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<CompletableFuture<ResponseEntity<ReservationResponse>>> futures = new ArrayList<>();

        futures.add(CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(getBaseUrl(), HttpMethod.POST,
                        new HttpEntity<>(request1, createHeaders()), ReservationResponse.class), executor));
        futures.add(CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(getBaseUrl(), HttpMethod.POST,
                        new HttpEntity<>(request2, createHeaders()), ReservationResponse.class), executor));
        futures.add(CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(getBaseUrl(), HttpMethod.POST,
                        new HttpEntity<>(request3, createHeaders()), ReservationResponse.class), executor));
        futures.add(CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(getBaseUrl(), HttpMethod.POST,
                        new HttpEntity<>(request4, createHeaders()), ReservationResponse.class), executor));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        int expectedListSize = 2;
        // waiting for persistence
        org.awaitility.Awaitility.await().atMost(Duration.ofSeconds(5)).await().untilAsserted(() -> {
            List<Reservation> reservations = reservationRepository.findAll();
            assertThat(reservations).hasSize(expectedListSize);
        });
    }

    @Test
    @DisplayName("""
            Given an invalid reservation request
            When POST /api/reservation/v1/ is called
            Then should return bad request
            """)
    void reserve_WithInvalidRequest_ShouldReturnBadRequest() {
        ReservationRequest request = new ReservationRequest();

        ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()),
                ReservationResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("""
            Given a valid reservation ID
            When DELETE /api/reservation/v1/{id} is called
            Then should delete reservation successfully
            """)
    void deleteReservation_WithValidId_ShouldDeleteSuccessfully() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String username = "testUser" + uuid;

        String reservationId = "testUser:300";
        int expectedListSize = 0;

        Users user = createUser(username, "test_" + uuid + "@example.com");
        user = userRepository.save(user);

        AvailableSlots slot = createAvailableSlot(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        slot = slotsRepository.save(slot);

        Reservation reservation = new Reservation();
        reservation.setReservationId(reservationId);
        reservation.setSlot(slot);
        reservation.setUser(user);
        reservationRepository.save(reservation);

        ResponseEntity response = restTemplate.exchange(
                getBaseUrl() + reservationId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders()),
                ResponseEntity.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(expectedListSize);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private void cleanDatabase() {
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

    private void executeSqlScript(String scriptPath) throws Exception {
        Resource resource = new ClassPathResource(scriptPath);
        ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
    }

}
