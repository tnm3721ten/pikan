package com.example.pikan.enumtype;

// enumは、定数をまとめて管理するためのもの。（列挙型ともいうらしい）
public enum HatenaStatus {
	OPEN, //これ自体が、1つの完成したオブジェクト
	RESOLVED
}


/*enumのメソッド
values(): 「登録されている選択肢を全部教えて！」と頼むと、リスト（配列）にして返してくれる。HTMLでセレクトボックスを作るときに便利です。
name(): そのピースの「名前（"WHY"）」を文字列として取り出せる。
ordinal(): そのピースが「上から何番目か」を数字で教えてくれる（0から始まる）。
*/