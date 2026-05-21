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
	//and ( :q is null or trim(:q) = '' or ...like... ): 渡されてきたqがnullの時、空文字の時、ちゃんと文字の時のみ検索を実行する。
	//lower(h.content): content を小文字化
	//Like: 文字列がパターンに合うかをチェックする。「%」は任意の文字列を表す。つまり、今回は、「わたってきたqの前後に何があってもいいよ（部分一致検索をするよ）」という意味
	//concat('%', :q, '%'):concatは文字列をくっつける。
	//coalesce :左から順に見て、最初に null じゃない値を返す関数。
	//coalesce(h.answer, ''):引数を左から順に見て、最初に現れた「null ではない値」を返す。二個目の引数は定数の空文字なので 常に null じゃない → '' を返す
	//select / where には書いていませんが、findForHome(..., sort) を呼ぶと、そのSortに従って並び替えされまる。これは Spring Data JPA の仕組み。

	//何故クエリが二つあるのかというと、以前は1本のクエリに「q が null のときは検索しない」ような以下の条件を書いていたが、
	//PostgreSQL側で trim(:q) や lower(...) が bytea 型として解釈され、function lower(bytea) does not exist のような SQL エラー → 500 になっていた。

	//このクエリを呼び出す前に、qがないことは確認しているため、qについての言及がない。
	@Query("""
			select h
			from Hatena h
			where h.status = :status
			  and (:type is null or h.type = :type)
			""")
	List<Hatena> findForHomeWithoutQuery(
			@Param("status") HatenaStatus status,
			@Param("type") HatenaType type,
			Sort sort);

	@Query("""
			select h
			from Hatena h
			where h.status = :status
			  and (:type is null or h.type = :type)
			  and (
			      lower(h.content) like lower(concat('%', :q, '%'))
			      or lower(coalesce(h.answer, '')) like lower(concat('%', :q, '%'))
			  )
			""")
	List<Hatena> findForHomeWithQuery(
			@Param("status") HatenaStatus status,
			@Param("type") HatenaType type,
			@Param("q") String q,
			Sort sort);

		//サービスなどから findForHome(...) を呼ぶ→Spring Data JPA が そのメソッドに付いている @Query の SQL/JPQL を実行する。→条件に合う行が List<Hatena> として返る。
		//status 引数 → :status     type 引数 → :type     q 引数 → :q     sort 引数 → クエリ外で ORDER BY として効く（自動で並び替え）


		//①アプリ起動時　Spring Boot が起動し、@SpringBootApplication配下などをスキャンする。　HatenaRepositoryはinterfaceなので、Spring Data JPAが実装付きのプロキシ（Bean） を生成してコンテナに登録する。
		//②@Service などが HatenaRepository を コンストラクタ注入 / フィールド注入 で受け取る。コンテナが すでに作っておいた Bean を渡すだけ（この時点でも検索はしない）。
		//③処理の途中でメソッドが呼ばれる
		//④呼ばれた瞬間に Spring Data が動く　@Query の JPQL を使い、:status :type :q に 引数をバインド する。Sort があれば ORDER BY を付ける（クエリ文字列に書いていなくても）。
		//⑤JPA（Hibernate など）が DB にアクセス　JPQL が 実際の SQL に近い形に変換され、DB に送られる。結果の行が Hatena にマッピング され、List<Hatena> になる。
}
