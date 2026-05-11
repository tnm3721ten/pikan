package com.example.pikan.controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pikan.entity.Hatena;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaCreateForm;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.service.HatenaService;

import jakarta.validation.Valid;

@Controller
public class HatenaController {

	// 守るために１工程追加
	private final HatenaService hatenaService;

	// HatenaServiceを使うためのコンストラクタ。
	public HatenaController(HatenaService hatenaService) {
		this.hatenaService = hatenaService;
	}

	// 引数を見て自動判断してくれている。
	// Modelのオブジェクトは引数に入れた時点で自動で作られる。
	// Modelはフロントエンドにデータを渡す・もらうためのオブジェクト。データを渡すときは addAttribute、もらうときは getAttribute。
	// statusがnullかどうかのチェックは、/をベタ打ちしたときにデフォルトの画面を表示するために必要。nullはDBにあるはずがないので、参照しても何も出てこない。
	@GetMapping("/")
	public String index(
			@RequestParam(name = "status", required = false) HatenaStatus status,
			@RequestParam(name = "q", required = false) String q,
			@RequestParam(name = "type", required = false) String type,
			Model model) {
		HatenaStatus effectiveStatus = (status == null) ? HatenaStatus.OPEN : status;

		HatenaType effectiveType = null;
		if (type != null && !type.isBlank()) {
			try {
				effectiveType = HatenaType.valueOf(type.trim());
			} catch (IllegalArgumentException ignored) {
				effectiveType = null;
			}
		}

		List<Hatena> hatenaList = hatenaService.findForHome(effectiveStatus, effectiveType, q);
		model.addAttribute("hatenaList", hatenaList);
		model.addAttribute("currentStatus", effectiveStatus);
		model.addAttribute("q", q);
		model.addAttribute("currentType", effectiveType);
		// index.htmlに渡すという意味。
		return "index";
	}

	// はてなの分類（5W1H）選択画面
	@GetMapping("/select-type")
	public String selectType() {
		return "select-type";
	}

	// type を受け取って「入力画面」を表示する（ちゃんとenumにないものがあれば、エラーを出してくれる）
	// 自分で new Model() することは絶対にない.だから引数に入れている
	// Formに入れて、Modelに詰めてreturmすることで、次の画面に渡している。
	@GetMapping("/create")
	public String create(@RequestParam("type") HatenaType type, Model model) {
		HatenaCreateForm form = new HatenaCreateForm();
		form.setType(type);
		model.addAttribute("form", form);
		return "create";
	}

	// 引数の@Valid: メソッドが実行される直前に、Spring Bootが背後で「バリデーション・エンジン（検査員）」を呼び出す。
	//        　　　 検査員は HatenaCreateForm の設計図を見て、@NotBlank や @Size のルールを一つずつチェックする。
	// 引数のBindingResult:　引数に BindingResult が書いてあると、検査員は「エラーがあってもプログラムを止めずに、このノート（BindingResult）にメモして、そのままメソッドを動かしていいよ」という特別なモードに切り替わる。
	//                      つまり、エラーの一覧を取得するために必要ということ。
	// bindingResult.hasErrors(): 検査の結果、一つでもダメな項目があれば true になる
	@PostMapping("/save")
	public String save(@Valid HatenaCreateForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("form", form);
			return "create";
		}
		hatenaService.saveNew(form.getType(), form.getContent());
		return "redirect:/";
	}

	//　詳細画面（SCR-04）
	//　詰め替えはserviceで行う。そのため、formを使って詰め替えを行う。
	//　formは入力用。hatenaは出力用。
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		Hatena hatena = hatenaService.findById(id);
		HatenaUpdateForm form = hatenaService.toUpdateForm(hatena);
		model.addAttribute("form", form);
		model.addAttribute("hatena", hatena);
		return "detail";
	}

	/*更新するメソッド
	  @Valid により、HatenaUpdateFormのフィールドについているバリデーション（例: @NotNull, @Size など）を使って入力チェックが走る。
	  BindingResult bindingResult: @Validの検証結果（エラー情報）を受け取る入れ物。
	                               BindingResult は、@Valid を付けた引数（この場合 form）の直後に置く必要がある。
	  bindingResult: @Valid で検証した結果（どの項目がどんな理由でNGか）を持っているオブジェクト→必ず、Modelnに追加されて勝手にわたる。なので、書かなくてよい。
	*/
	@PostMapping("/update")
	public String update(@Valid HatenaUpdateForm form, BindingResult bindingResult, Model model) {
		Hatena hatena = hatenaService.findById(form.getId());

		if (bindingResult.hasErrors()) {
			model.addAttribute("form", form);
			model.addAttribute("hatena", hatena);
			return "detail";
		}

		hatenaService.update(form);
		return "redirect:/detail/" + form.getId();
	}


	//@RequestParam("id") Long id: hiddenで送られてきたものを引数の数字に入れている。
	@PostMapping("/delete")
	public String delete(@RequestParam("id") Long id) {
		hatenaService.delete(id);
		return "redirect:/";
	}
}

