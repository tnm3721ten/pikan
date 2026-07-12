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

	public void validate(RegisterForm form, BindingResult bindingResult) {
		validateField(form, bindingResult, "username");
		validateField(form, bindingResult, "password");
	}

	private void validateField(RegisterForm form, BindingResult bindingResult, String fieldName) {
		if (validateProperty(form, bindingResult, fieldName, RegisterFormGroups.Required.class)) {
			return;
		}
		if (validateProperty(form, bindingResult, fieldName, RegisterFormGroups.Length.class)) {
			return;
		}
		validateProperty(form, bindingResult, fieldName, RegisterFormGroups.Format.class);
	}

	private boolean validateProperty(
			RegisterForm form,
			BindingResult bindingResult,
			String fieldName,
			Class<?> group) {
		Set<ConstraintViolation<RegisterForm>> violations = validator.validateProperty(form, fieldName, group);
		for (ConstraintViolation<RegisterForm> violation : violations) {
			bindingResult.rejectValue(
					fieldName,
					violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
					violation.getMessage());
		}
		return !violations.isEmpty();
	}
}
