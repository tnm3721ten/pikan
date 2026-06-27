package com.example.pikan.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.pikan.repository.UserRepository;

@Service
// DBのusersテーブルをもとに、Spring Security用のUserDetailsを作成する。
public class PikanUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public PikanUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// usernameでユーザーを検索し、認証に必要なusername/passwordをUserDetailsに詰め替える。
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.example.pikan.entity.User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.build();
	}
}
