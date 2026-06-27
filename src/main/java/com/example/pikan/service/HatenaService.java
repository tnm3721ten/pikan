package com.example.pikan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.pikan.entity.Hatena;
import com.example.pikan.entity.User;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.repository.HatenaRepository;
import com.example.pikan.repository.UserRepository;

@Service
public class HatenaService {

	private final HatenaRepository hatenaRepository;

	private final UserRepository userRepository;

	public HatenaService(HatenaRepository hatenaRepository, UserRepository userRepository) {
		this.hatenaRepository = hatenaRepository;
		this.userRepository = userRepository;
	}


	// ログイン中ユーザーのはてなだけをホーム一覧に表示する。
	// q が空の場合はcontent/answer検索を行わず、nullパラメータによるDB側の型推論エラーを避ける。
	public List<Hatena> findForHome(String username, HatenaStatus status, HatenaType type, String query) {
		User user = getCurrentUser(username);
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		String effectiveQuery = (query == null || query.isBlank()) ? null : query.trim();
		if (effectiveQuery == null) {
			return hatenaRepository.findForHomeWithoutQuery(user, status, type, sort);
		}
		return hatenaRepository.findForHomeWithQuery(user, status, type, effectiveQuery, sort);
	}


	// 詳細、更新、削除の際にログイン中のはてな１件を取得する。
	public Hatena findById(Long id, String username) {
		User user = getCurrentUser(username);
		return hatenaRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new IllegalArgumentException("指定されたはてなが見つかりません: id=" + id));
	}



	// 詳細画面の更新フォーム用に、Hatena Entityを入力フォームへ詰め替える。
	public HatenaUpdateForm toUpdateForm(Hatena hatena) {
		HatenaUpdateForm form = new HatenaUpdateForm();
		form.setId(hatena.getId());
		form.setContent(hatena.getContent());
		form.setAnswer(hatena.getAnswer());
		form.setResolved(hatena.getStatus() == HatenaStatus.RESOLVED);
		return form;
	}


	//更新フォームの内容で、ログイン中ユーザー所有のはてなを更新する
	public Hatena update(HatenaUpdateForm form, String username) {
		Hatena hatena = findById(form.getId(), username);

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

	public void delete(Long id, String username) {
		Hatena hatena = findById(id, username);
		hatenaRepository.delete(hatena);
	}

	// 新規作成時にログイン中ユーザーを所有者として紐づけ、未解決状態で保存する。
	public Hatena saveNew(HatenaType type, String content, String username) {
		User user = getCurrentUser(username);
		LocalDateTime now = LocalDateTime.now();
		Hatena hatena = new Hatena();
		hatena.setUser(user);
		hatena.setType(type);
		hatena.setContent(content);
		hatena.setCreatedAt(now);
		hatena.setUpdatedAt(now);
		hatena.setStatus(HatenaStatus.OPEN);
		hatena.setResolvedAt(null);
		hatena.setAnswer(null);
		return hatenaRepository.save(hatena);
	}

	// Spring Securityから受け取ったusernameから、DB上のUser Entityを取得する。
	private User getCurrentUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("ログイン中のユーザーが見つかりません: " + username));
	}
}
