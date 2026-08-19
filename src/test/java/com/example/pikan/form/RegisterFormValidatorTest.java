package com.example.pikan.form;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * RegisterFormの入力ルールと、RequiredがLength/Formatより先にreturnすることを確認する。
 * Springを起動せず、本物のJakartaValidatorを手で渡す。
 */
class RegisterFormValidatorTest {

	private RegisterFormValidator registerFormValidator;

	@BeforeEach
	void setUp() {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		registerFormValidator = new RegisterFormValidator(validator);
	}

	@Test
	void username_blank_hasRequiredError() {
		RegisterForm form = formWithUsername("");
		// Web では Spring が作るエラー箱を、単体テストでは自分で用意する。
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

	@Test
	void password_blank_hasRequiredError() {
		RegisterForm form = formWithPassword("");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(passwordErrorMessage(bindingResult)).isEqualTo("パスワードを入力してください");
	}

	@Test
	void password_7chars_hasLengthError() {
		RegisterForm form = formWithPassword("a".repeat(7));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(passwordErrorMessage(bindingResult))
				.isEqualTo("パスワードは8文字以上50文字以内で入力してください");
	}

	@Test
	void password_8chars_hasNoPasswordError() {
		RegisterForm form = formWithPassword("a".repeat(8));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(bindingResult.getFieldErrors("password")).isEmpty();
	}

	@Test
	void password_50chars_hasNoPasswordError() {
		RegisterForm form = formWithPassword("a".repeat(50));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(bindingResult.getFieldErrors("password")).isEmpty();
	}

	@Test
	void password_51chars_hasLengthError() {
		RegisterForm form = formWithPassword("a".repeat(51));
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(passwordErrorMessage(bindingResult))
				.isEqualTo("パスワードは8文字以上50文字以内で入力してください");
	}

	@Test
	void password_nonAlphanumeric_hasFormatError() {
		RegisterForm form = formWithPassword("pass!word");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(passwordErrorMessage(bindingResult)).isEqualTo("半角英数字のみ使用できます");
	}

	@Test
	void username_blank_hasOnlyRequiredError() {
		RegisterForm form = formWithUsername("");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		// Requiredでreturnするため、Length/Formatは付かない
		assertThat(bindingResult.getFieldErrors("username")).hasSize(1);
		assertThat(usernameErrorMessage(bindingResult)).isEqualTo("ユーザー名を入力してください");
	}

	@Test
	void password_blank_hasOnlyRequiredError() {
		RegisterForm form = formWithPassword("");
		BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");

		registerFormValidator.validate(form, bindingResult);

		assertThat(bindingResult.getFieldErrors("password")).hasSize(1);
		assertThat(passwordErrorMessage(bindingResult)).isEqualTo("パスワードを入力してください");
	}

	//ユーザー名だけ変え、パスワードは常に有効値にする（ユーザー名のエラーだけ見やすくするため）
	private RegisterForm formWithUsername(String username) {
		RegisterForm form = new RegisterForm();
		form.setUsername(username);
		form.setPassword("password1");
		return form;
	}

	//パスワードだけ変え、ユーザー名は常に有効値にする
	private RegisterForm formWithPassword(String password) {
		RegisterForm form = new RegisterForm();
		form.setUsername("validuser");
		form.setPassword(password);
		return form;
	}

	private String usernameErrorMessage(BindingResult bindingResult) {
		FieldError error = bindingResult.getFieldError("username");
		assertThat(error).isNotNull();
		return error.getDefaultMessage();
	}

	private String passwordErrorMessage(BindingResult bindingResult) {
		FieldError error = bindingResult.getFieldError("password");
		assertThat(error).isNotNull();
		return error.getDefaultMessage();
	}
}
