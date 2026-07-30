package com.example.pikan.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.pikan.entity.User;

//UserRepository（本物）、User Entity と JPA、H2（組み込み DB）を起動する。小さな箱に入れる。
@DataJpaTest
//application-{プロファイル名}.propertiesのプロファイル名がtestとかいてあれば、そのファイルが読まれる。
@ActiveProfiles("test")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void findByUsername_returnsSavedUser() {
		User user = new User();
		user.setUsername("repo-user");
		user.setPassword("encoded-password");
		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		userRepository.save(user);

		Optional<User> found = userRepository.findByUsername("repo-user");

		assertThat(found).isPresent();
		assertThat(found.get().getUsername()).isEqualTo("repo-user");
		assertThat(found.get().getPassword()).isEqualTo("encoded-password");
	}
}
