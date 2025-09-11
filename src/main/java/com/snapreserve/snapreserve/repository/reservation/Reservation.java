package com.snapreserve.snapreserve.repository.reservation;

import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_reservation_id", columnList = "reservationId"),
})
@Data
public class Reservation {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private AvailableSlots slot;

    @Column
    @CreationTimestamp
    private LocalDateTime created_at;

}
