package com.myflavor.myflavor.domain.profile.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;
import com.myflavor.myflavor.domain.profile.dto.ProfileResponseDTO;
import com.myflavor.myflavor.domain.profile.model.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final MainFeedRepository mainFeedRepository;

	public ResponseEntity<ProfileResponseDTO> getProfiles(String userName, Pageable pageable) {
		Page<MainFeedDTO> feeds = mainFeedRepository.findByUser_Name(userName, pageable).map(MainFeedDTO::new);
		int followers = followRepository.countByFollower_Name(userName);
		int following = followRepository.countByFollowing_Name(userName);

		System.out.println(followers + " " + following);

		ProfileResponseDTO responseDTO = ProfileResponseDTO.builder()
			.followingCnt(following)
			.follwerCnt(followers)
			.feedCnt(1)
			.feeds(feeds.map(FeedResponseDTO::new))
			.build();

		return ResponseEntity.ok().body(responseDTO);
	}
}
