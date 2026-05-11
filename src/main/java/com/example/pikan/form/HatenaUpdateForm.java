package com.example.pikan.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HatenaUpdateForm {

	@NotNull
	private Long id;

	/*@Pattern(regexp = "(?s).*\\S.*", message = "はてなを入力してください")
	　(?s): 「DOTALLモード」。.（ドット）が改行 \n にもマッチするようになります
	　.*: . は「任意の1文字」、* は「直前の要素を0回以上」つまり .* は「任意の文字が任意の回数」
	　\S: 「空白文字ではない1文字」
	　「.*\\S.*」は、「任意の文字が0回以上」（前後のなんでもOK部分）と「空白文字ではない1文字」が1回以上。
	*/

	@Pattern(regexp = "(?s).*\\S.*", message = "はてなを入力してください") // 「(?s)」は、「.」が改行文字を含むことを示すためのフラグ。
	@Size(max = 1000, message = "1000文字以内で入力してください")
	private String content;

	@Size(max = 2000, message = "回答は2000文字以内で入力してください")
	private String answer;

	private boolean resolved;
}

