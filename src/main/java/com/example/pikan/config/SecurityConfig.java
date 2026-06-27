package com.example.pikan.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/register").permitAll()
						.requestMatchers(HttpMethod.POST, "/register").permitAll()
						.requestMatchers("/css/**").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							response.sendRedirect("/login?required");
						}))
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/", true)
						.failureHandler((request, response, exception) -> {
							String username = request.getParameter("username");
							String redirectUrl = "/login?error";
							if (username != null && !username.isBlank()) {
								redirectUrl += "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
							}
							response.sendRedirect(redirectUrl);
						}))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login"));

		return http.build();
	}
}
