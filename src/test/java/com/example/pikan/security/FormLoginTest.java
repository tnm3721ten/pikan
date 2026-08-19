package com.example.pikan.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pikan.config.SecurityConfig;
import com.example.pikan.controller.AuthController;
import com.example.pikan.form.RegisterFormValidator;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.RegisterService;

/*
 * フォームログイン（POST /login）の成功・失敗を確認する。
 * DB は使わず、テスト用の InMemoryUserDetailsManager を使う。
 */
@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, FormLoginTest.TestUserConfig.class })
class FormLoginTest {

	@Autowired
	private MockMvc mockMvc;

	// AuthController の依存。このテストでは呼ばないのでモックで箱を満たす。
	@MockitoBean
	private RegisterService registerService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private RegisterFormValidator registerFormValidator;

	@TestConfiguration
	static class TestUserConfig {

		@Bean
		@Primary
		UserDetailsService testUserDetailsService(PasswordEncoder passwordEncoder) {
			UserDetails user = User.withUsername("testuser")
					.password(passwordEncoder.encode("password1"))
					.roles("USER")
					.build();
			return new InMemoryUserDetailsManager(user);
		}
	}

	@Test
	void login_withValidCredentials_redirectsToHome() throws Exception {
		mockMvc.perform(post("/login")
						.with(csrf())
						.param("username", "testuser")
						.param("password", "password1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void login_withInvalidCredentials_redirectsToLoginError() throws Exception {
		// 失敗時は failureHandler が /login?error&username=... へ送る
		mockMvc.perform(post("/login")
						.with(csrf())
						.param("username", "testuser")
						.param("password", "wrong-password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error&username=testuser"));
	}
}
