package com.example.pikan.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, max = 20)
	@Column(name = "username", nullable = false, length = 20, unique = true)
	private String username;

	@NotBlank
	@Size(max = 255)
	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
