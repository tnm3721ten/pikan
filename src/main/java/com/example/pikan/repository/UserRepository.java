package com.example.pikan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pikan.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// ログイン認証や所有者チェックで使うユーザー情報をusernameから取得する。
	Optional<User> findByUsername(String username);
}
