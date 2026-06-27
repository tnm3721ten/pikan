package com.example.pikan.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pikan.entity.User;
import com.example.pikan.form.RegisterForm;
import com.example.pikan.repository.UserRepository;

@Service
public class RegisterService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 新規ユーザー登録時に、パスワードをハッシュ化してusersテーブルへ保存する。
	public void register(RegisterForm form) {
		User user = new User();
		user.setUsername(form.getUsername());
		user.setPassword(passwordEncoder.encode(form.getPassword()));

		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		userRepository.save(user);
	}
}
