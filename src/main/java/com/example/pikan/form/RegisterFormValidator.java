package com.example.pikan.form;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class RegisterFormValidator {

	private final Validator validator;

	public RegisterFormValidator(Validator validator) {
		this.validator = validator;
	}

	// Required → Length → Format の順で検証し、先に失敗した段階だけエラーを返す。
	public void validate(RegisterForm form, BindingResult bindingResult) {
		if (validateGroup(form, bindingResult, RegisterFormGroups.Required.class)) {
			return;
		}
		if (validateGroup(form, bindingResult, RegisterFormGroups.Length.class)) {
			return;
		}
		validateGroup(form, bindingResult, RegisterFormGroups.Format.class);
	}

	private boolean validateGroup(RegisterForm form, BindingResult bindingResult, Class<?> group) {
		Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form, group);
		for (ConstraintViolation<RegisterForm> violation : violations) {
			bindingResult.rejectValue(
					violation.getPropertyPath().toString(),
					violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
					violation.getMessage());
		}
		return bindingResult.hasErrors();
	}
}
