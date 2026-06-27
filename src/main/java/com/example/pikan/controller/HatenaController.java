package com.example.pikan.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pikan.entity.Hatena;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaCreateForm;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.service.HatenaService;

import jakarta.validation.Valid;

@Controller
public class HatenaController {

	private static final String NOT_FOUND_MESSAGE = "指定されたはてなが見つかりません。";

	private static final String INVALID_TYPE_MESSAGE = "種類（type）が指定されていないか、不正です。";

	private static final String INVALID_ID_MESSAGE = "指定されたIDが不正です。";

	private static final String SAVE_SUCCESS_MESSAGE = "はてなを保存しました。";

	private static final String SAVE_FAILED_MESSAGE = "はてなの保存に失敗しました。";

	private static final String UPDATE_SUCCESS_MESSAGE = "はてなを更新しました。";

	private static final String UPDATE_FAILED_MESSAGE = "はてなの更新に失敗しました。";

	private static final String DELETE_SUCCESS_MESSAGE = "はてなを削除しました。";

	private static final String DELETE_FAILED_MESSAGE = "はてなの削除に失敗しました。";

	// 守るために１工程追加
	private final HatenaService hatenaService;

	// HatenaServiceを使うためのコンストラクタ。
	public HatenaController(HatenaService hatenaService) {
		this.hatenaService = hatenaService;
	}


	// ホーム一覧を表示する。URLパラメータは不正値でも落ちないようenumに変換する。
	@GetMapping("/")
	public String index(
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "q", required = false) String q,
			@RequestParam(name = "type", required = false) String type,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {

		// statusが未入力・不正値の場合は、OPEN（未解決）一覧を表示する。
		HatenaStatus effectiveStatus = HatenaStatus.OPEN;
		if (status != null && !status.isBlank()) {
			try {
				effectiveStatus = HatenaStatus.valueOf(status.trim());
			} catch (IllegalArgumentException ignored) {
				effectiveStatus = HatenaStatus.OPEN;
			}
		}

		// typeが未指定または不正値の場合は、種別で絞り込まない。
		HatenaType effectiveType = null;
		if (type != null && !type.isBlank()) {
			try {
				effectiveType = HatenaType.valueOf(type.trim());
			} catch (IllegalArgumentException ignored) {
				effectiveType = null;
			}
		}

		List<Hatena> hatenaList = hatenaService.findForHome(userDetails.getUsername(), effectiveStatus, effectiveType, q);
		model.addAttribute("currentUsername", userDetails.getUsername());
		model.addAttribute("hatenaList", hatenaList);
		model.addAttribute("currentStatus", effectiveStatus);
		model.addAttribute("q", q);
		model.addAttribute("currentType", effectiveType);
		return "index";
	}


	// はてなの分類（5W1H）選択画面を表示する。
	@GetMapping("/select-type")
	public String selectType() {
		return "select-type";
	}


	// 作成画面は種別選択後に表示するため、typeが不正な場合はホームへ戻す。
	@GetMapping("/create")
	public String create(
			@RequestParam(name = "type", required = false) String type,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (type == null || type.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", INVALID_TYPE_MESSAGE);
			return "redirect:/";
		}

		HatenaType hatenaType;
		try {
			hatenaType = HatenaType.valueOf(type.trim());
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", INVALID_TYPE_MESSAGE);
			return "redirect:/";
		}

		HatenaCreateForm form = new HatenaCreateForm();
		form.setType(hatenaType);
		model.addAttribute("form", form);
		return "create";
	}

	// 作成フォームの入力を検証し、問題なければログイン中ユーザーのはてなとして保存する。
	@PostMapping("/save")
	public String save(
			@Valid @ModelAttribute("form") HatenaCreateForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "create";
		}
		try {
			hatenaService.saveNew(form.getType(), form.getContent(), userDetails.getUsername());
			redirectAttributes.addFlashAttribute("successMessage", SAVE_SUCCESS_MESSAGE);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", SAVE_FAILED_MESSAGE);
		}
		return "redirect:/";
	}


	// 指定されたIDのはてなを取得し、詳細画面を表示する。
	@GetMapping("/detail/{id}")
	public String detail(
			@PathVariable("id") String id,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {
		Long parsedId;
		try {
			parsedId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			redirectAttributes.addFlashAttribute("errorMessage", INVALID_ID_MESSAGE);
			return "redirect:/";
		}

		Hatena hatena;
		try {
			hatena = hatenaService.findById(parsedId, userDetails.getUsername());
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", NOT_FOUND_MESSAGE);
			return "redirect:/";
		}

		// 画面表示用の Hatena と、更新フォーム用の form をそれぞれ渡す。
		HatenaUpdateForm form = hatenaService.toUpdateForm(hatena);
		model.addAttribute("form", form);
		model.addAttribute("hatena", hatena);
		return "detail";
	}

	// 更新フォームを検証し、問題なければログイン中ユーザーのはてなとして更新する。
	@PostMapping("/update")
	public String update(
			@Valid @ModelAttribute("form") HatenaUpdateForm form, 
			BindingResult bindingResult,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			RedirectAttributes redirectAttributes) {
		Hatena hatena;
		try {
			hatena = hatenaService.findById(form.getId(), userDetails.getUsername());
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", NOT_FOUND_MESSAGE);
			return "redirect:/";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("hatena", hatena);
			return "detail";
		}

		try {
			// 更新可否の判断と保存はService側で行う。
			hatenaService.update(form, userDetails.getUsername());
			redirectAttributes.addFlashAttribute("successMessage", UPDATE_SUCCESS_MESSAGE);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", UPDATE_FAILED_MESSAGE);
		}
		return "redirect:/";
	}


	// 指定されたIDのはてなを削除する。
	@PostMapping("/delete")
	public String delete(
			@RequestParam("id") String id,
			@AuthenticationPrincipal UserDetails userDetails,
			RedirectAttributes redirectAttributes) {
		Long parsedId;
		try {
			parsedId = Long.parseLong(id);
		} catch (NumberFormatException e) {
			redirectAttributes.addFlashAttribute("errorMessage", INVALID_ID_MESSAGE);
			return "redirect:/";
		}

		try {
			// 削除可否の判断はService側で行う。
			hatenaService.delete(parsedId, userDetails.getUsername());
			redirectAttributes.addFlashAttribute("successMessage", DELETE_SUCCESS_MESSAGE);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", NOT_FOUND_MESSAGE);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", DELETE_FAILED_MESSAGE);
		}
		return "redirect:/";
	}
}

