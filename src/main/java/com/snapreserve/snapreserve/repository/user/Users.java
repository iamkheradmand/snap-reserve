package com.snapreserve.snapreserve.repository.user;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
		@Index(name = "idx_users_username", columnList = "username")
})
@Data
@Builder
public class Users {

	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column
	@CreationTimestamp
	private LocalDateTime created_at;

}
