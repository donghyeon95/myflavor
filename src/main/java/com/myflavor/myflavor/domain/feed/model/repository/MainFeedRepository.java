package com.myflavor.myflavor.domain.feed.model.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;

public interface MainFeedRepository extends JpaRepository<MainFeed, Long> {
	@Query("SELECT new com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO(mf.id, mf.createdAt, mf.updatedAt, mf.title, mf.feedPhoto, mf.visitMethod, mf.content, mf.restaurantId, mf.heartCnt, mf.subFeeds) FROM MainFeed mf JOIN mf.user u where u.name = :userName")
	Page<MainFeedDTO> findByUserName(String userName, Pageable pageable);

	@Query(
		value =
			"SELECT DISTINCT mf "
				+
				"FROM MainFeed mf " +
				"LEFT JOIN fetch mf.subFeeds sf " +
				"LEFT JOIN FETCH  mf.user u " +
				"WHERE mf.id = :id")
	Optional<MainFeed> findByIdToMainFeedDTO(long id);

}