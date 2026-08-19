package com.example.pikan.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import com.example.pikan.config.SecurityConfig;
import com.example.pikan.entity.User;
import com.example.pikan.form.RegisterForm;
import com.example.pikan.form.RegisterFormValidator;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.RegisterService;

/**
 * POST /register の成功・入力エラー・重複を確認する。
 * Validator / Repository / Service はモックする。
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegisterService registerService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private RegisterFormValidator registerFormValidator;

	@Test
	void registerForm_returnsRegisterView() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("form"));
	}

	@Test
	void registerSubmit_validForm_redirectsToLogin() throws Exception {
		// DBは使わず、未登録（empty）をモックで作る。
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

		// CSRFなしのPOSTはSecurityに弾かれる。
		// Validatorはモックなのでvalidateは何もしない→BindingResultは空のまま。
		mockMvc.perform(post("/register")
						.with(csrf())
						.param("username", "newuser")
						.param("password", "password1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		verify(registerService).register(any(RegisterForm.class));
	}

	@Test
	void registerSubmit_validationError_returnsRegisterViewAndDoesNotProceed() throws Exception {
		// 本物のValidatorは使わず、モックがBindingResultにエラーを入れる。
		doAnswer(invocation -> {
			BindingResult bindingResult = invocation.getArgument(1);
			bindingResult.rejectValue("username", "NotBlank", "ユーザー名を入力してください");
			return null;
		}).when(registerFormValidator).validate(any(RegisterForm.class), any(BindingResult.class));

		mockMvc.perform(post("/register")
						.with(csrf())
						.param("username", "")
						.param("password", "password1"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"));

		// 入力エラーなら重複確認・登録に進まない
		verify(userRepository, never()).findByUsername(any());
		verify(registerService, never()).register(any(RegisterForm.class));
	}

	@Test
	void registerSubmit_duplicateUsername_returnsRegisterViewAndDoesNotRegister() throws Exception {
		User existingUser = new User();
		existingUser.setUsername("newuser");
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(existingUser));

		mockMvc.perform(post("/register")
						.with(csrf())
						.param("username", "newuser")
						.param("password", "password1"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrorCode("form", "username", "duplicate"));

		verify(registerService, never()).register(any(RegisterForm.class));
	}
}
