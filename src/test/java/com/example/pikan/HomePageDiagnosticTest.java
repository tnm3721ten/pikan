package com.example.pikan;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.service.HatenaService;

@SpringBootTest
class HomePageDiagnosticTest {

	@Autowired
	private HatenaService hatenaService;

	@Test
	void findForHomeDoesNotThrow() {
		assertThatCode(() -> hatenaService.findForHome(HatenaStatus.OPEN, null, null))
				.doesNotThrowAnyException();
	}
}
