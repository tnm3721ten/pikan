package com.example.pikan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pikan.entity.Hatena;
import com.example.pikan.entity.User;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.repository.HatenaRepository;
import com.example.pikan.repository.UserRepository;

/**
 * Hatenaの保存・取得・更新・削除を、DBなしで確認する。
 */
@ExtendWith(MockitoExtension.class)
class HatenaServiceTest {

	@Mock
	private HatenaRepository hatenaRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private HatenaService hatenaService;

	@Test
	void saveNew_setsLoggedInUserAsOwnerWithOpenInitialState() {
		User owner = new User();
		owner.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(owner));

		hatenaService.saveNew(HatenaType.WHAT, "本文", "testuser");

		//hatenaCaptorは記録したものを入れる箱
		ArgumentCaptor<Hatena> hatenaCaptor = ArgumentCaptor.forClass(Hatena.class);
		verify(hatenaRepository).save(hatenaCaptor.capture());

		//isSameAs(x)同じインスタンス（==）、isEqualTo(x)同じ値（equals）
		Hatena saved = hatenaCaptor.getValue();
		assertThat(saved.getUser()).isSameAs(owner);
		assertThat(saved.getType()).isEqualTo(HatenaType.WHAT);
		assertThat(saved.getContent()).isEqualTo("本文");
		assertThat(saved.getStatus()).isEqualTo(HatenaStatus.OPEN);
		assertThat(saved.getAnswer()).isNull();
		assertThat(saved.getResolvedAt()).isNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();
	}

	@Test
	void findById_returnsHatenaWhenIdAndOwnerMatch() {
		User owner = new User();
		owner.setUsername("testuser");
		Hatena ownHatena = new Hatena();
		ownHatena.setId(1L);
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(owner));
		when(hatenaRepository.findByIdAndUser(1L, owner)).thenReturn(Optional.of(ownHatena));

		Hatena found = hatenaService.findById(1L, "testuser");

		assertThat(found).isSameAs(ownHatena);
		verify(hatenaRepository).findByIdAndUser(1L, owner);
	}

	@Test
	void findById_throwsWhenNotFoundForCurrentUser() {
		User owner = new User();
		owner.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(owner));
		when(hatenaRepository.findByIdAndUser(2L, owner)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> hatenaService.findById(2L, "testuser"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("2");

		verify(hatenaRepository).findByIdAndUser(2L, owner);
	}

	@Test
	void update_replacesContentAndKeepsNonBlankAnswer() {
		User owner = owner();
		Hatena hatena = existingHatena(owner);
		LocalDateTime previousUpdatedAt = hatena.getUpdatedAt();
		stubFindOwnedHatena(owner, hatena);

		hatenaService.update(updateForm("新しい本文", "新しい回答"), "testuser");

		Hatena saved = capturedSave();
		assertThat(saved.getContent()).isEqualTo("新しい本文");
		assertThat(saved.getAnswer()).isEqualTo("新しい回答");
		assertThat(saved.getUpdatedAt()).isAfter(previousUpdatedAt);
	}

	@Test
	void update_convertsEmptyAnswerToNull() {
		User owner = owner();
		Hatena hatena = existingHatena(owner);
		stubFindOwnedHatena(owner, hatena);

		hatenaService.update(updateForm("新しい本文", ""), "testuser");

		assertThat(capturedSave().getAnswer()).isNull();
	}

	@Test
	void update_convertsBlankAnswerToNull() {
		User owner = owner();
		Hatena hatena = existingHatena(owner);
		stubFindOwnedHatena(owner, hatena);

		hatenaService.update(updateForm("新しい本文", "   "), "testuser");

		assertThat(capturedSave().getAnswer()).isNull();
	}

	private User owner() {
		User owner = new User();
		owner.setUsername("testuser");
		return owner;
	}

	private Hatena existingHatena(User owner) {
		Hatena hatena = new Hatena();
		hatena.setId(1L);
		hatena.setUser(owner);
		hatena.setContent("旧本文");
		hatena.setAnswer("旧回答");
		hatena.setStatus(HatenaStatus.OPEN);
		hatena.setUpdatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
		return hatena;
	}

	private void stubFindOwnedHatena(User owner, Hatena hatena) {
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(owner));
		when(hatenaRepository.findByIdAndUser(hatena.getId(), owner)).thenReturn(Optional.of(hatena));
	}

	private HatenaUpdateForm updateForm(String content, String answer) {
		HatenaUpdateForm form = new HatenaUpdateForm();
		form.setId(1L);
		form.setContent(content);
		form.setAnswer(answer);
		form.setResolved(false);
		return form;
	}

	private Hatena capturedSave() {
		ArgumentCaptor<Hatena> hatenaCaptor = ArgumentCaptor.forClass(Hatena.class);
		verify(hatenaRepository).save(hatenaCaptor.capture());
		return hatenaCaptor.getValue();
	}
}
