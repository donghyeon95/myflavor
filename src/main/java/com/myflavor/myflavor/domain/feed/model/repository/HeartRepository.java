package com.myflavor.myflavor.domain.feed.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.model.Heart;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;

import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Table(
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"user_id", "main_feed_id"})
		}
)
public interface HeartRepository extends JpaRepository<Heart, Long> {
	public Boolean existsByMainFeedAndUser( MainFeed mainFeed, User user);

	public Long countByMainFeed(MainFeed mainFeed);
}
