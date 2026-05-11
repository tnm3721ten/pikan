package com.example.pikan.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pikan.entity.Hatena;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;


// < > の中は、<Entity, IDの型>を指定する。
public interface HatenaRepository extends JpaRepository<Hatena, Long> {
	List<Hatena> findByStatus(HatenaStatus status, Sort sort);
	
	//クエリ文においてテーブル名ではなくてentity名。カラム名ではなくてentityのフィールド名。
	//where 条件 は「その条件が true の行だけ返す」クエリ文に該当するところだけ、返すという意味

	//@Query(""" ... """): 「このメソッドは、下に書いたクエリ文で検索してね」という指定
	//select h: Hatenaエンティティを h という名前で取るし返すときもそう。返すものを決めている。一個のデータ
	//from Hatena h: Hatenaエンティティ（＝テーブル相当）から探す。どこからとるかを決めている。一個のデータ
	//where h.status = :status: Hatenaクラスのstatusフィールドが:statusという値と等しい。
      /*whereの後には、条件を書く。
	    =: 等しいという意味。比較をしている。
	    :status: 条件側（引数の値）　　　h.status = 比較される側（DBの値）*/
	//and (:type is null or h.type = :type): 「type は任意」type が null なら: 条件を無効化（全部OK）           type が nullじゃないなら: h.type = type のものだけ
	  /*and: 直前の条件に「さらに条件を追加」（両方満たす必要がある）
	    or: 直前の条件に「さらに条件を追加」（どちらかが満たせばOK）
		:type: パラメータ（メソッド引数 @Param("type") HatenaType type の値）
		is: 「〜である」判定で使う（ここでは null 判定）*/
	//and ( :q is null or trim(:q) = '' or ...like... ): qがnullの時、空文字の時、ちゃんと文字の時のみ検索を実行する。
	//lower(h.content): content を小文字化
	//concat('%', :q, '%'): 「% + q + %」→ qを含むパターン
	//lower(coalesce(h.answer, '')) like ...:「answer が null でも落ちないように '' に置き換えてから検索」

	@Query("""
			select h
			from Hatena h
			where h.status = :status
			  and (:type is null or h.type = :type)
			  and (
			      :q is null
			      or trim(:q) = ''
			      or lower(h.content) like lower(concat('%', :q, '%'))
			      or lower(coalesce(h.answer, '')) like lower(concat('%', :q, '%'))
			  )
			""")
	List<Hatena> findForHome(
			@Param("status") HatenaStatus status,
			@Param("type") HatenaType type,
			@Param("q") String q,
			Sort sort);
}
