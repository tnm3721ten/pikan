package com.example.pikan.entity;

import java.time.LocalDateTime;

import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
// @Dataは、getter, setter, toString, equals, hashCodeを自動生成するためのアノテーション。
// @Entityは、JPAのエンティティクラスであることを示すためのアノテーション。
// @Tableは、テーブル名を指定するためのアノテーション。「SQL で作った hatena という名前のノートに対応させてね」という指示。
// @Idは、主キーを指定するためのアノテーション。
// @GeneratedValueは、主キーの生成方式を指定するためのアノテーション。
// @Columnは、カラム名を指定するためのアノテーション。
// @Enumeratedは、列挙型を指定するためのアノテーション。
// @NotBlankは、空文字を禁止するためのアノテーション。
// @NotNullは、nullを禁止するためのアノテーション。
// @Sizeは、文字列の長さを制限するためのアノテーション。
// @ColumnDefinitionは、カラムの定義を指定するためのアノテーション。
// @Enumerated(EnumType.STRING)は、列挙型を文字列で保存するためのアノテーション。
// @Enumerated(EnumType.ORDINAL)は、列挙型を数値で保存するためのアノテーション。
// @Enumerated(EnumType.STRING)は、列挙型を文字列で保存するためのアノテーション。
@Data
@Entity
@Table(name = "hatena")
public class Hatena {

	// strategy = GenerationType.IDENTITY というのは、PostgreSQLの BIGSERIAL（自動採番）という機能と仲良くするための「合言葉」
	// Longを使うのは、BIGSERIALの型と合わせるため。いっぱい数字を使うので、Longを使う。
	// privateにしているのは、外部からアクセスできないようにするため。getter, setterを使ってアクセスする。
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// NotNullでJavaのNULLを禁止して、nullable = falseは、SQLのNULLを禁止する。
	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// Enumerated(EnumType.STRING)はStringとして保存させるためのアノテーション。
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 20)
	private HatenaType type;

	@NotBlank
	@Size(max = 1000)
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private HatenaStatus status;

	@Size(max = 2000)
	@Column(name = "answer", columnDefinition = "TEXT")
	private String answer;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;
}

