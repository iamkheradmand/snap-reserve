package com.snapreserve.snapreserve.repository.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import com.snapreserve.snapreserve.repository.sluts.AvailableSlots;
import com.snapreserve.snapreserve.repository.user.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.data.annotation.Id;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
@Data
public class Reservation {

	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column (nullable = false, unique = true)
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
