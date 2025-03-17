package com.myflavor.myflavor.domain.feed.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;

public interface SubFeedRepository extends JpaRepository<SubFeed, Long> {
	void deleteAllByMainFeed(MainFeed mainFeed);
}
