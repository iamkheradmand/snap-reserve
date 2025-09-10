package com.snapreserve.snapreserve.repository.sluts;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableSlotsRepository extends JpaRepository<AvailableSlots, Long> {
	@Query("SELECT t FROM AvailableSlots t WHERE t.is_reserved = false ORDER BY t.start_time ASC")
	Slice<AvailableSlots> findAvailableSlots(Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM AvailableSlots s WHERE s.id = :id")
	Optional<AvailableSlots> findByIdForUpdate(@Param("id") Long id);
}