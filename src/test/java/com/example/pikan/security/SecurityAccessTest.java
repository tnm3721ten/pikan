package com.example.pikan.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pikan.config.SecurityConfig;
import com.example.pikan.controller.AuthController;
import com.example.pikan.form.RegisterFormValidator;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.RegisterService;

/*
 * 未認証アクセスで、公開URLと保護URLの違いを確認する。
 * @WithMockUserなし = 未ログイン。
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class SecurityAccessTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegisterService registerService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private RegisterFormValidator registerFormValidator;

	@Test
	void login_isAccessibleWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	void register_isAccessibleWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"));
	}

	@Test
	void home_redirectsToLoginRequiredWithoutAuthentication() throws Exception {
		// "/"はanyRequest().authenticated()。未ログインならEntryPointで/login?required
		mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?required"));
	}
}
