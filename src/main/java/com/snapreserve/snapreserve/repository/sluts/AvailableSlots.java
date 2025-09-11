package com.snapreserve.snapreserve.repository.sluts;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "available_slots")
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

