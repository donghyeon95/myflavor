package com.myflavor.myflavor.domain.profile.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.domain.profile.dto.FollowDTO;
import com.myflavor.myflavor.domain.profile.model.entity.Follow;
import com.myflavor.myflavor.domain.profile.model.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	@Transactional
	public ResponseEntity<String> follow(String followerName, String followingName) {
		if (followerName.equals(followingName)) {
			throw new IllegalArgumentException("You cannot follow yourself.");
		}

		User follower = userRepository.findByName(followerName).orElseThrow();
		User following = userRepository.findByName(followingName).orElseThrow();
		Follow newFollow = Follow.builder()
			.follower(follower)
			.following(following)
			.build();

		followRepository.save(newFollow);

		return ResponseEntity.ok().body("SUCCESS");
	}

	@Transactional
	public ResponseEntity<String> unFollow(String followerName, String followingName) {
		User follower = userRepository.findByName(followerName).orElseThrow();
		User following = userRepository.findByName(followingName).orElseThrow();

		followRepository.deleteFollowByFollowingAndFollower(following, follower);

		return ResponseEntity.ok().body("SUCCESS");
	}

	public ResponseEntity<Page<FollowDTO>> getFollowList(String userName, Pageable pageable) {

		System.out.println(userName);
		Page<FollowDTO> userList = followRepository.findByFollower_Name(userName, pageable).map(FollowDTO::new);
		return ResponseEntity.ok().body(userList);
	}

}
