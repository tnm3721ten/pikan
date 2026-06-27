package com.example.pikan.entity;

import java.time.LocalDateTime;

import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;


@Data
@Entity
@Table(name = "hatena")
public class Hatena {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 20)
	private HatenaType type;

	@NotBlank
	@Size(max = 1000)
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private HatenaStatus status;

	@Size(max = 2000)
	@Column(name = "answer", columnDefinition = "TEXT")
	private String answer;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	// はてなの所有者。user_id外部キーをUser Entityとして扱う。
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}

