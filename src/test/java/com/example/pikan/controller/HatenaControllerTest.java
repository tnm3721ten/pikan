package com.example.pikan.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pikan.config.SecurityConfig;
import com.example.pikan.entity.Hatena;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;
import com.example.pikan.form.HatenaUpdateForm;
import com.example.pikan.service.HatenaService;

/**
 * GET / の一覧表示を、DBなしで確認する。
 */
@WebMvcTest(HatenaController.class)
@Import(SecurityConfig.class)
class HatenaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HatenaService hatenaService;

	@Test
	@WithMockUser(username = "testuser")
	void index_returnsIndexViewWhenAuthenticatedWithoutParams() throws Exception {
		List<Hatena> hatenaList = List.of();
		when(hatenaService.findForHome("testuser", HatenaStatus.OPEN, null, null))
				.thenReturn(hatenaList);

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("currentUsername", "testuser"))
				.andExpect(model().attribute("currentStatus", HatenaStatus.OPEN))
				.andExpect(model().attribute("hatenaList", hatenaList))
				.andExpect(model().attributeDoesNotExist("q", "currentType"));

		verify(hatenaService).findForHome("testuser", HatenaStatus.OPEN, null, null);
	}

	@Test
	@WithMockUser(username = "testuser")
	void index_returnsIndexViewWhenAuthenticatedWithSearchParams() throws Exception {
		List<Hatena> hatenaList = List.of();
		when(hatenaService.findForHome("testuser", HatenaStatus.RESOLVED, HatenaType.WHAT, "検索語"))
				.thenReturn(hatenaList);

		mockMvc.perform(get("/")
						.param("status", "RESOLVED")
						.param("type", "WHAT")
						.param("q", "検索語"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attribute("currentUsername", "testuser"))
				.andExpect(model().attribute("hatenaList", hatenaList))
				.andExpect(model().attribute("currentStatus", HatenaStatus.RESOLVED))
				.andExpect(model().attribute("currentType", HatenaType.WHAT))
				.andExpect(model().attribute("q", "検索語"));

		verify(hatenaService).findForHome("testuser", HatenaStatus.RESOLVED, HatenaType.WHAT, "検索語");
	}

	@Test
	@WithMockUser(username = "testuser")
	void detail_returnsDetailViewWhenAuthenticatedWithValidId() throws Exception {
		Hatena hatena = new Hatena();
		HatenaUpdateForm form = new HatenaUpdateForm();

		when(hatenaService.findById(1L, "testuser")).thenReturn(hatena);
		when(hatenaService.toUpdateForm(hatena)).thenReturn(form);

		mockMvc.perform(get("/detail/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("detail"))
				.andExpect(model().attribute("hatena", hatena))
				.andExpect(model().attribute("form", form));

		verify(hatenaService).findById(1L, "testuser");
		verify(hatenaService).toUpdateForm(hatena);
	}

	@Test
	@WithMockUser(username = "testuser")
	void detail_redirectsToHomeWhenFindByIdThrowsIllegalArgumentException() throws Exception {
		when(hatenaService.findById(1L, "testuser")).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/detail/1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(flash().attribute("errorMessage", "指定されたはてなが見つかりません。"));

		verify(hatenaService).findById(1L, "testuser");
		verify(hatenaService, never()).toUpdateForm(any());
	}
}
