package com.example.pikan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.pikan.entity.Hatena;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.repository.HatenaRepository;

@Service
public class HatenaService {

	// finalにするのは、インスタンス変数を外部から変更できなくするため。
	private final HatenaRepository hatenaRepository;

	//　クラスのコンストラクタに書いてあったら、springが勝手にオブジェクトを作ってくれる。
	// Spring Bootが勝手に作った、HatenaRepositoryのオブジェクトをfinalの↑の変数に代入する。
	public HatenaService(HatenaRepository hatenaRepository) {
		this.hatenaRepository = hatenaRepository;
	}

	//全件取得メソッドを使って、HatenaRepositoryからHatenaのリストを取得する。
	//Sort.by(...)は、「～って感じで並べ替えてね」という指示。Sort.by( 並び順 , "Entityの変数名" )
	//Sort.Direction.DESCは、降順（新しい順）という指示。
	//List は入っているデータの順番を保持してくれるという特性があるため、戻り値は配列ではなくリスト。
	public List<Hatena> findAll() {
		return hatenaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
	}

	//　解決済みかのステータスによって絞り込むメソッド
	public List<Hatena> findByStatus(HatenaStatus status) {
		return hatenaRepository.findByStatus(status, Sort.by(Sort.Direction.DESC, "createdAt"));
	}

	public List<Hatena> findForHome(HatenaStatus status, HatenaType type, String query) {
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		return hatenaRepository.findForHome(status, type, query, sort);
	}


	//指定されたidのはてなを取得するメソッド。
	//.orElseThrow(...)「Optionalが空っぽなら、例外を投げろ（Throw）」という命令
	//() -> new IllegalArgumentException(...) データが見つかった場合は、この右側の new ... は一生実行されません。
	//new IllegalArgumentException("エラーメッセージ")
	public Hatena findById(Long id) {
		return hatenaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("指定されたはてなが見つかりません: id=" + id));
	}



	//HatenaをHatenaUpdateFormに詰め替えるメソッド。（表示用に詰め替える）
	//Formではチェックボックスでtrue, falseを受け取るが、Hatenaではenumで受け取る。→画面では2択なので、TFにした方がいい。
	public HatenaUpdateForm toUpdateForm(Hatena hatena) {
		HatenaUpdateForm form = new HatenaUpdateForm();
		form.setId(hatena.getId());
		form.setContent(hatena.getContent());
		form.setAnswer(hatena.getAnswer());
		form.setResolved(hatena.getStatus() == HatenaStatus.RESOLVED);
		return form;
	}


	/*更新作業
	　Formを受け取って、entityに詰め替えて、保存する。
	  hatenaRepository.save(hatena): 保存したentityを返す（将来遣うかもしれないから）
	 　　　　　　　　　　　　　　　　　 でも今回の設計だと、Controllerで受け取らずに捨てている形になっている
	*/
	public Hatena update(HatenaUpdateForm form) {
		Hatena hatena = findById(form.getId());

		LocalDateTime now = LocalDateTime.now();
		hatena.setContent(form.getContent());

		String answer = form.getAnswer();
		hatena.setAnswer(answer == null || answer.isBlank() ? null : answer);

		if (form.isResolved()) {
			hatena.setStatus(HatenaStatus.RESOLVED);
			hatena.setResolvedAt(now);

		} else {
			hatena.setStatus(HatenaStatus.OPEN);
			hatena.setResolvedAt(null);
		}

		hatena.setUpdatedAt(now);
		return hatenaRepository.save(hatena);
	}

	public void delete(Long id) {
		hatenaRepository.deleteById(id);
	}

	// 新しいHatenaを登録するメソッド。
	// 将来的に、登録した情報（id）を使うかもしれないので、Hatenaを返す。
	public Hatena saveNew(HatenaType type, String content) {
		LocalDateTime now = LocalDateTime.now();
		Hatena hatena = new Hatena();
		hatena.setType(type);
		hatena.setContent(content);
		hatena.setCreatedAt(now);
		hatena.setUpdatedAt(now);
		hatena.setStatus(HatenaStatus.OPEN);
		hatena.setResolvedAt(null);
		hatena.setAnswer(null);
		return hatenaRepository.save(hatena);
	}
}
