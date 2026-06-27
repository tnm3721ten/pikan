package com.example.pikan.form;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RegisterFormValidationTest {

	private RegisterFormValidator registerFormValidator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		registerFormValidator = new RegisterFormValidator(validator);
	}

	@Test
	void emptyFieldsShowOnlyRequiredErrors() {
		RegisterForm form = new RegisterForm();
		form.setUsername("");
		form.setPassword("");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		Set<String> messages = bindingResult.getFieldErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toSet());

		assertThat(messages).containsExactlyInAnyOrder(
				"ユーザー名を入力してください",
				"パスワードを入力してください");
	}

	@Test
	void shortUsernameShowsOnlyLengthError() {
		RegisterForm form = new RegisterForm();
		form.setUsername("ab");
		form.setPassword("password01");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		Set<String> usernameMessages = bindingResult.getFieldErrors("username").stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toSet());

		assertThat(usernameMessages).containsExactly("ユーザー名は3文字以上20文字以内で入力してください");
	}

	@Test
	void invalidCharactersShowOnlyFormatError() {
		RegisterForm form = new RegisterForm();
		form.setUsername("user 01");
		form.setPassword("password01");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		Set<String> usernameMessages = bindingResult.getFieldErrors("username").stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toSet());

		assertThat(usernameMessages).containsExactly("半角英数字のみ使用できます");
	}
}
