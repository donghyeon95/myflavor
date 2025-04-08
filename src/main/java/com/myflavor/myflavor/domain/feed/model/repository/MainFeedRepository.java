package com.myflavor.myflavor.domain.feed.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
		value =
			"SELECT DISTINCT mf " +
				"FROM MainFeed mf " +
				"LEFT JOIN FETCH mf.restaurant r " +
				"LEFT JOIN fetch mf.subFeeds sf " +
				"LEFT JOIN FETCH  mf.user u " +
				"WHERE mf.id IN :feedKeys"
	)
	List<MainFeed> findMainFeedByIds(@Param("feedKeys") List<String> feedKeys);

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
			   WITH Followees AS (
			        SELECT follower_id
			        FROM myflavor.follow f\s
			           JOIN user as u ON u.id =  f.following_id
			        WHERE u.name= :username
			      ),
			      FilteredFeed AS (
			        SELECT mf.*,\s
			               ROW_NUMBER() OVER (PARTITION BY mf.user_id ORDER BY mf.created_at DESC, mf.id DESC) AS rnk
			        FROM main_feed mf
			        JOIN Followees f ON mf.user_id = f.follower_id
			        WHERE mf.created_at > :time
			      )
			      SELECT *
			      FROM FilteredFeed
			      WHERE rnk <= 5
			""",
		nativeQuery = true
	)
	List<MainFeed> queryByUserFollower(@Param("username") String username, @Param("time") LocalDateTime time);

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