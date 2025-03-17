package com.myflavor.myflavor.domain.profile.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.profile.model.entity.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
	// 팔로워 목록 페이징
	Page<Follow> findByFollower_Name(String userName, Pageable pageable);

	int deleteFollowByFollowingAndFollower(User following, User follower);

	int countByFollowing_Name(String userName);

	int countByFollower_Name(String userName);
}
