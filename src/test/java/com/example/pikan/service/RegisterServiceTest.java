package com.example.pikan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.pikan.entity.User;
import com.example.pikan.form.RegisterForm;
import com.example.pikan.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private RegisterService registerService;

	@Test
	void register_encodesPasswordAndSavesUser() {
		RegisterForm form = new RegisterForm();
		form.setUsername("testuser");
		form.setPassword("password123");

		when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

		registerService.register(form);

		verify(passwordEncoder).encode("password123");

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		assertThat(savedUser.getUsername()).isEqualTo("testuser");
		assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
		assertThat(savedUser.getCreatedAt()).isNotNull();
		assertThat(savedUser.getUpdatedAt()).isNotNull();
	}
}