package com.example.pikan.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// はてな更新フォームの入力値とバリデーション定義。
@Data
public class HatenaUpdateForm {

	@NotNull
	private Long id;

	@Pattern(regexp = "(?s).*\\S.*", message = "はてなを入力してください")
	@Size(max = 1000, message = "1000文字以内で入力してください")
	private String content;

	@Size(max = 2000, message = "回答は2000文字以内で入力してください")
	private String answer;

	private boolean resolved;
}

