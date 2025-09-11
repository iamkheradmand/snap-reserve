package com.snapreserve.snapreserve.repository.sluts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "available_slots", indexes = {
        @Index(name = "idx_available_slots_is_reserved_start_time", columnList = "is_reserved, start_time")
})
@Data
public class AvailableSlots {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime start_time;

    @Column(nullable = false)
    private LocalDateTime end_time;

    @Column(nullable = false)
    private boolean is_reserved = false;

}

