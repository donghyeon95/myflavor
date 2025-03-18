package com.myflavor.myflavor.domain.feed.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;

public interface MainFeedRepository extends JpaRepository<MainFeed, Long> {
	// @Query("SELECT new com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO(mf.id, mf.createdAt, mf.updatedAt, mf.title, mf.feedPhoto, mf.visitMethod, mf.content, mf.restaurantId, mf.heartCnt, mf.configurations, mf.user ,mf.subFeeds) FROM MainFeed mf JOIN mf.user u where u.name = :userName")
	Page<MainFeed> findByUser_Name(String userName, Pageable pageable);

	@Query(
		value =
			"SELECT DISTINCT mf "
				+
				"FROM MainFeed mf " +
				"LEFT JOIN FETCH mf.restaurant r " +
				"LEFT JOIN fetch mf.subFeeds sf " +
				"LEFT JOIN FETCH  mf.user u " +
				"WHERE mf.id = :id")
	Optional<MainFeed> findByIdToMainFeedDTO(long id);

	@Query(
		value = "SELECT m " +
			"FROM MainFeed m " +
			"WHERE :subFeed MEMBER OF m.subFeeds"
	)
	Optional<MainFeed> queryByMember(SubFeed subFeed);

	/**
	 * 내 팔로워의 최신 글 쿼리
	 * */
	@Query(
		value = """
			    WITH RecentAuthors AS (
			        SELECT DISTINCT mf1.user_id, mf1.created_at
			        FROM main_feed mf1
			        JOIN follow f ON mf1.user_id = f.following_id
			        JOIN user u ON f.follower_id = u.id
			        WHERE u.name = :username
			        AND mf1.created_at >= :time
			        ORDER BY mf1.created_at DESC
			        LIMIT 1000
			    ),
			    RankedPosts AS (
			        SELECT mf2.id, mf2.created_at, mf2.updated_at, mf2.title, mf2.feed_photo, mf2.visit_method,
			               mf2.content, mf2.restaurant_pk, mf2.heart_cnt, mf2.user_id,
			               ROW_NUMBER() OVER (PARTITION BY mf2.user_id ORDER BY mf2.created_at DESC) AS `rank`
			        FROM main_feed mf2
			        WHERE mf2.user_id IN (SELECT user_id FROM RecentAuthors)
			        AND mf2.created_at >= :time
			    )
			    SELECT * FROM RankedPosts WHERE `rank` <= 5;
			""",
		nativeQuery = true
	)
	List<MainFeed> queryByUserFollower(String username, LocalDateTime time);

	/**
	 * 특정 유저를 기준으로 그들이 쓴 최신 글 쿼리
	 * */
	List<MainFeed> findByUser_NameInAndCreatedAtAfterOrderByCreatedAtDesc(List<String> userNames,
		LocalDateTime createdAfter);

	/**
	 * 특정 카테고를 기준으로 최신 글 쿼리
	 * */
	List<MainFeed> findByRestaurant_RestaurantCategory_CategoryNameInAndCreatedAtAfterOrderByCreatedAtDesc(
		List<String> categories, LocalDateTime createdAfter);
}