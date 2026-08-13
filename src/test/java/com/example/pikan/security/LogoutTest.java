package com.example.pikan.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pikan.config.SecurityConfig;
import com.example.pikan.controller.AuthController;
import com.example.pikan.form.RegisterFormValidator;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.RegisterService;

/**
 * POST /logout と CSRF を確認する。
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class LogoutTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegisterService registerService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private RegisterFormValidator registerFormValidator;

	//CSRF付きでログアウトしたときのリダイレクトURL確認
	@Test
	@WithMockUser
	void logout_withCsrf_redirectsToLogin() throws Exception {
		mockMvc.perform(post("/logout")
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	//CSRFなしでログアウトしたときのステータス確認
	@Test
	@WithMockUser
	void logout_withoutCsrf_returnsForbidden() throws Exception {
		mockMvc.perform(post("/logout"))
				.andExpect(status().isForbidden());
	}
}
