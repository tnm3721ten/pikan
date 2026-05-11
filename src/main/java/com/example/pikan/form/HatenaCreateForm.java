package com.example.pikan.form;

import com.example.pikan.enumtype.HatenaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HatenaCreateForm {

	@NotNull
	private HatenaType type;

	@NotBlank
	@Size(max = 1000)
	private String content;
}

