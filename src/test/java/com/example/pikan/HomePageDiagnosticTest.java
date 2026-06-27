package com.example.pikan;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pikan.entity.User;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.HatenaService;

@SpringBootTest
class HomePageDiagnosticTest {

	@Autowired
	private HatenaService hatenaService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void findForHomeDoesNotThrow() {
		String username = "diagnostic-user";
		userRepository.findByUsername(username)
				.orElseGet(() -> {
					LocalDateTime now = LocalDateTime.now();
					User user = new User();
					user.setUsername(username);
					user.setPassword("password");
					user.setCreatedAt(now);
					user.setUpdatedAt(now);
					return userRepository.save(user);
				});

		assertThatCode(() -> hatenaService.findForHome(username, HatenaStatus.OPEN, null, null))
				.doesNotThrowAnyException();
	}
}
