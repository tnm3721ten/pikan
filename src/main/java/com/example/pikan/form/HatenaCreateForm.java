package com.example.pikan.form;

import com.example.pikan.enumtype.HatenaType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// はてな新規作成フォームの入力値とバリデーション定義。
@Data
public class HatenaCreateForm {

	@NotNull
	private HatenaType type;

	@NotNull(message = "はてなを入力してください")
	@Pattern(regexp = "(?s).*\\S.*", message = "はてなを入力してください")
	@Size(max = 1000, message = "1000文字以内で入力してください")
	private String content;
}

