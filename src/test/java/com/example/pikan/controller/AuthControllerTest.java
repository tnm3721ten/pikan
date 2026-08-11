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

//Controller テスト用の Web 層だけ の Spring 箱を起動する。
//AuthController だけを 本物の Bean として Spring に載せる。
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	//RegisterService 等の 偽物を Spring の Bean として登録する。
	@MockitoBean
	private RegisterService registerService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private RegisterFormValidator registerFormValidator;

	@Test
	void registerForm_returnsRegisterView() throws Exception {
		mockMvc.perform(get("/register"))
			//HTTP レスポンス（ステータス、View 名、Model）を assert。リクエストなので、assertThatじゃなくて、andExpect。
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("form"));
	}

	@Test
	void registerSubmit_validForm_redirectsToLogin() throws Exception {
		// 未登録なら empty。DBは使わずモックで「いない」状態にする。
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

		// CSRF なしの POST は Security に弾かれるので .with(csrf()) を付ける。
		// RegisterFormValidator はモックのため validate は何もしない → BindingResult は空のまま。
		mockMvc.perform(post("/register")
						.with(csrf())
						.param("username", "newuser")
						.param("password", "password1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));

		verify(registerService).register(any(RegisterForm.class));
	}

	//空のユーザー名を入力した時に登録画面に遷移し、重複確認と登録の処理を行わない
	@Test
	void registerSubmit_validationError_returnsRegisterView() throws Exception {
		// 本物の Validator は使わず、モックが BindingResult にエラーを入れる。
		//「偽物の validate が呼ばれたら、代わりにこの処理をやれ」と登録する→普段と逆の表記
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
		// verify(確認対象のモック, never()).呼ばれていないはずのメソッド(引数条件);
		verify(userRepository, never()).findByUsername(any());
		verify(registerService, never()).register(any(RegisterForm.class));
	}

	//ユーザー名が重複した際、登録画面に遷移し、エラーを表示。登録処理を行わない
	@Test
	void registerSubmit_duplicateUsername_returnsRegisterView() throws Exception {
		User existingUser = new User();
		existingUser.setUsername("newuser");
		// 既存ユーザーあり → isPresent() が true
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(existingUser));

		mockMvc.perform(post("/register")
						.with(csrf())
						.param("username", "newuser")
						.param("password", "password1"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				//.andExpect(model().attributeHasFieldErrorCode(モデル名, フィールド名, エラーコード))
				.andExpect(model().attributeHasFieldErrorCode("form", "username", "duplicate"));

		verify(registerService, never()).register(any(RegisterForm.class));
	}
}
