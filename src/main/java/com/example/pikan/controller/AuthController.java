package com.example.pikan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pikan.form.RegisterForm;
import com.example.pikan.form.RegisterFormValidator;
import com.example.pikan.repository.UserRepository;
import com.example.pikan.service.RegisterService;

@Controller
public class AuthController {

	private static final String REGISTER_SUCCESS_MESSAGE = "登録しました。ログインしてください。";

	private static final String DUPLICATE_USERNAME_MESSAGE = "このユーザー名は既に使われています";

	private final RegisterService registerService;

	private final UserRepository userRepository;

	private final RegisterFormValidator registerFormValidator;

	public AuthController(
			RegisterService registerService,
			UserRepository userRepository,
			RegisterFormValidator registerFormValidator) {
		this.registerService = registerService;
		this.userRepository = userRepository;
		this.registerFormValidator = registerFormValidator;
	}

	// ログイン画面を表示する。認証失敗時は入力済みusernameを再表示する。
	@GetMapping("/login")
	public String login(
			@RequestParam(name = "username", required = false) String username,
			Model model) {
		model.addAttribute("username", username != null ? username : "");
		return "login";
	}

	// 登録画面の初期表示用に、空のフォームを用意する
	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("form", new RegisterForm());
		return "register";
	}

	// 登録フォームの送信時に、ユーザー登録を行う。失敗時は登録画面にフォワード。
	@PostMapping("/register")
	public String registerSubmit(
			@ModelAttribute("form") RegisterForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		registerFormValidator.validate(form, bindingResult);
		if (bindingResult.hasErrors()) {
			return "register";
		}

		// 入力形式に問題がなければ、DBを使ってusernameの重複を確認する。
		if (userRepository.findByUsername(form.getUsername()).isPresent()) {
			bindingResult.rejectValue("username", "duplicate", DUPLICATE_USERNAME_MESSAGE);
			return "register";
		}

		registerService.register(form);
		redirectAttributes.addFlashAttribute("successMessage", REGISTER_SUCCESS_MESSAGE);
		return "redirect:/login";
	}
}
