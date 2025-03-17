package com.myflavor.myflavor.domain.profile.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myflavor.myflavor.domain.profile.dto.FollowDTO;
import com.myflavor.myflavor.domain.profile.service.FollowService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "/follow", produces = "application/json")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@PostMapping("/{followerName}/{followingName}")
	public ResponseEntity<String> follwUser(@PathVariable String followerName, @PathVariable String followingName) {
		followService.follow(followerName, followingName);

		return ResponseEntity.ok().body("");
	}

	@DeleteMapping("/{followerName}/{followingName}")
	public ResponseEntity<String> unFollowUser(@PathVariable String followerName, @PathVariable String followingName) {
		followService.unFollow(followerName, followingName);
		return ResponseEntity.ok().body("");
	}

	/**
	 * 특정 사용자의 팔로워 목록 조회
	 */
	@GetMapping("/{userName}/followers")
	public ResponseEntity<Page<FollowDTO>> getfollowers(@PathVariable String userName, @RequestParam("s") int s,
		@RequestParam("p") int p) {

		Pageable pageable = PageRequest.of(p, s);
		return followService.getFollowList(userName, pageable);
	}
}
