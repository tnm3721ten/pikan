package com.example.pikan.form;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class RegisterFormValidatorTest {

	private RegisterFormValidator registerFormValidator;

	@BeforeEach
	void setUp() {
		// 本番と同じく、本物の Jakarta Validator を使う（モックしない）
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		registerFormValidator = new RegisterFormValidator(validator);
	}

	@Test
	void username_blank_hasRequiredError() {
		RegisterForm form = formWithUsername("");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(usernameErrorMessage(bindingResult)).isEqualTo("ユーザー名を入力してください");
	}

	@Test
	void username_2chars_hasLengthError() {
		RegisterForm form = formWithUsername("ab");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(usernameErrorMessage(bindingResult))
				.isEqualTo("ユーザー名は3文字以上20文字以内で入力してください");
	}

	@Test
	void username_3chars_hasNoUsernameError() {
		RegisterForm form = formWithUsername("abc");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(bindingResult.getFieldErrors("username")).isEmpty();
	}

	@Test
	void username_20chars_hasNoUsernameError() {
		RegisterForm form = formWithUsername("a".repeat(20));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(bindingResult.getFieldErrors("username")).isEmpty();
	}

	@Test
	void username_21chars_hasLengthError() {
		RegisterForm form = formWithUsername("a".repeat(21));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(usernameErrorMessage(bindingResult))
				.isEqualTo("ユーザー名は3文字以上20文字以内で入力してください");
	}

	@Test
	void username_nonAlphanumeric_hasFormatError() {
		RegisterForm form = formWithUsername("user!");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(usernameErrorMessage(bindingResult)).isEqualTo("半角英数字のみ使用できます");
	}

	/** ユーザー名だけ変え、パスワードは常に有効値にする（ユーザー名のエラーだけ見やすくするため） */
	private RegisterForm formWithUsername(String username) {
		RegisterForm form = new RegisterForm();
		form.setUsername(username);
		form.setPassword("password1");
		return form;
	}

	private String usernameErrorMessage(BindingResult bindingResult) {
		FieldError error = bindingResult.getFieldError("username");
		assertThat(error).isNotNull();
		return error.getDefaultMessage();
	}
}
