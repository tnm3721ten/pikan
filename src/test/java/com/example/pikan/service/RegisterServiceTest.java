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

//このクラス内の @Test が Mockito 対応になる
@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

	//UserRepository や PasswordEncoder の 偽物 を Mockito が作る。
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	//RegisterService を作り、コンストラクタに @Mock を渡す。
	@InjectMocks
	private RegisterService registerService;

	@Test
	void register_encodesPasswordAndSavesUser() {
		RegisterForm form = new RegisterForm();
		form.setUsername("testuser");
		form.setPassword("password123");

		when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

		registerService.register(form);

		//モックに「このメソッドが呼ばれたか」を確認させる。
		verify(passwordEncoder).encode("password123");

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		//Java オブジェクト（User）の フィールド値 を assert。
		assertThat(savedUser.getUsername()).isEqualTo("testuser");
		assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
		assertThat(savedUser.getCreatedAt()).isNotNull();
		assertThat(savedUser.getUpdatedAt()).isNotNull();
	}
}