package com.example.pikan.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DateTimeFormatsTest {

	@Test
	void formatReturnsDisplayPattern() {
		LocalDateTime dateTime = LocalDateTime.of(2026, 5, 28, 13, 14);
		assertThat(DateTimeFormats.format(dateTime)).isEqualTo("2026/05/28 13:14");
	}

	@Test
	void formatReturnsEmptyStringForNull() {
		assertThat(DateTimeFormats.format(null)).isEmpty();
	}
}
