package com.myflavor.myflavor.domain.feed.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myflavor.myflavor.domain.feed.model.DTO.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;

public interface MainFeedRepository extends JpaRepository<MainFeed, Long> {
	@Query("SELECT new com.myflavor.myflavor.domain.feed.model.DTO.MainFeedDTO(mf.id, mf.createdAt, mf.updatedAt, mf.title, mf.feedPhoto, mf.visitMethod, mf.content, mf.restaurantId, mf.heartCnt) FROM MainFeed mf JOIN mf.user u where u.name = :userName")
	Page<MainFeedDTO> findByUserName(String userName, Pageable pageable);
}
