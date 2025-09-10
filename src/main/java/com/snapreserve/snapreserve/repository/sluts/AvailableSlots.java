package com.snapreserve.snapreserve.repository.sluts;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;

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

