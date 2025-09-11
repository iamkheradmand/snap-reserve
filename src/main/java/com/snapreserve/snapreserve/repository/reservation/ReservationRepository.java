package com.snapreserve.snapreserve.repository.reservation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Modifying
	@Query("DELETE FROM Reservation r WHERE r.reservationId = :reservationId")
	void deleteByReservationId(@Param("reservationId") String reservationId);

	Optional<Reservation> findByReservationId(String reservationId);

}