package com.example.pikan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pikan.entity.Hatena;
import com.example.pikan.entity.User;
import com.example.pikan.enumtype.HatenaStatus;
import com.example.pikan.enumtype.HatenaType;


public interface HatenaRepository extends JpaRepository<Hatena, Long> {
	List<Hatena> findByStatus(HatenaStatus status, Sort sort);
	
	// ホーム一覧用。検索キーワードがない場合は文字列検索を行わず、ユーザー・ステータス・種別で絞り込む。
	@Query("""
			select h
			from Hatena h
			where h.user = :user
			  and h.status = :status
			  and (:type is null or h.type = :type)
			""")
	List<Hatena> findForHomeWithoutQuery(
			@Param("user") User user,
			@Param("status") HatenaStatus status,
			@Param("type") HatenaType type,
			Sort sort);

	// ホーム一覧用。content/answerを大文字小文字を区別せず部分一致検索する。
	// answerは未回答でnullになり得るため、空文字に置き換えて検索対象にする。
	@Query("""
			select h
			from Hatena h
			where h.user = :user
			  and h.status = :status
			  and (:type is null or h.type = :type)
			  and (
			      lower(h.content) like lower(concat('%', :q, '%'))
			      or lower(coalesce(h.answer, '')) like lower(concat('%', :q, '%'))
			  )
			""")
	List<Hatena> findForHomeWithQuery(
			@Param("user") User user,
			@Param("status") HatenaStatus status,
			@Param("type") HatenaType type,
			@Param("q") String q,
			Sort sort);

	// 詳細・更新・削除時の所有者チェック用。
	Optional<Hatena> findByIdAndUser(Long id, User user);

}
