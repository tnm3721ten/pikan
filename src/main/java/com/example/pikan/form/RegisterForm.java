package com.example.pikan.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ユーザー登録フォームの入力値とバリデーション定義。
@Data
public class RegisterForm {

	@NotBlank(message = "ユーザー名を入力してください", groups = RegisterFormGroups.Required.class)
	@Size(min = 3, max = 20, message = "ユーザー名は3文字以上20文字以内で入力してください", groups = RegisterFormGroups.Length.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字のみ使用できます", groups = RegisterFormGroups.Format.class)
	private String username;

	@NotBlank(message = "パスワードを入力してください", groups = RegisterFormGroups.Required.class)
	@Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以内で入力してください", groups = RegisterFormGroups.Length.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字のみ使用できます", groups = RegisterFormGroups.Format.class)
	private String password;
}
