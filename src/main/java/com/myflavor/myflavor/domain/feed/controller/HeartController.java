package com.myflavor.myflavor.domain.feed.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.JWT.JwtProvider;
import com.myflavor.myflavor.domain.feed.service.HeartService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/heart", produces = "application/json")
public class HeartController {

	private JwtProvider jwtProvider;
	private HeartService heartService;

	public HeartController(JwtProvider jwtProvider, HeartService heartService) {
		this.jwtProvider = jwtProvider;
		this.heartService = heartService;
	}

	@GetMapping("/up/feed")
	public void upFeed(@RequestParam("id") long feedId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		heartService.upFeedHeart(feedId, userName);
	}

	@GetMapping("/down/feed")
	public void downFeed() {
	}

	@GetMapping("/up/comment")
	public void upComment() {
	}

	@GetMapping("/down/comment")
	public void downComment() {
	}
}
