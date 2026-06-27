package com.example.pikan.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//フォーマット用のクラス。LocalDateTimeをStringに変換するために使われる
public final class DateTimeFormats {

	//ofPattern(...) は その設計図どおりのフォーマッター（オブジェクト）を作るメソッド
	private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

	//newするのを防ぐためにprivateにしている。
	private DateTimeFormats() {
	}

	public static String format(LocalDateTime dateTime) {
		if (dateTime == null) {
			return "";
		}
		return DISPLAY.format(dateTime);
	}
}
