package com.myflavor.myflavor.domain.feed.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.JWT.JwtProvider;
import com.myflavor.myflavor.domain.feed.model.DTO.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.model.DTO.Picture;
import com.myflavor.myflavor.domain.feed.model.DTO.FeedHTTPVO;
import com.myflavor.myflavor.domain.feed.model.DTO.SettingKey;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;
import com.myflavor.myflavor.domain.feed.service.FeedService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping(value = "/feed", produces = "application/json")
public class FeedController {

	private FeedService feedService;
	private JwtProvider jwtProvider;

	public FeedController(FeedService feedService, JwtProvider jwtProvider) {
		this.feedService = feedService;
		this.jwtProvider = jwtProvider;
	}

	@GetMapping
	public List<Object> getFeed(@RequestParam("id") long feedId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);

		feedService.getFeed(feedId, userName);
		return null;
	}

	@GetMapping("/list")
	public Page<MainFeedDTO> getFeeds(@RequestParam("p") int page, @RequestParam("s") int size,
			HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);

		Pageable pageable = PageRequest.of(page, size);
		return feedService.getFeedList(userName, pageable);
	}

	@PostMapping
	public void postFeed(@RequestParam(value = "id") long feedId, @RequestBody FeedHTTPVO feedHTTPVO,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO postHTTPDTO를 분리하기 위해서 VO를 새로이 만든다.

		String userName = jwtProvider.getUserNameFromRequest(request);
		feedService.insertUpdateFeed(feedId, userName, feedHTTPVO);
	}

	@PatchMapping
	public void patchFeed(@RequestParam(value = "id") long feedId, @RequestBody FeedHTTPVO feedHTTPVO) {

	}

	@DeleteMapping
	public void deleteFeed(@RequestParam(value = "id") long feedId) {
	}

	@GetMapping("/key")
	public long getFeedKey() {
		// 새로운 feed를 만들기 위한 Feed Id를 발급
		// 해당 Feed Id를 활용하여 mysql, redis => 공통적으로 사용???
		long id = feedService.generateId();
		System.out.println(id);
		return id;
	}

	// 유저가 임시 저장한 Feed의 목록을 가져 온다.
	@GetMapping("/temp/ids")
	public Set<Object> getTempFeedIds(HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		return feedService.getTempFeedIds(userName);
	}

	@GetMapping("/temp")
	public FeedHTTPVO getTempFeed(@RequestParam("id") long feedId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		feedService.getTempFeed(feedId, userName);
		return null;
	}

	@PostMapping("/temp")
	public void insertTempFeed(@RequestParam(value = "id") long feedId, @RequestBody FeedHTTPVO feedHTTPVO,
			HttpServletRequest request) {
		// TODO: 최초 생성을 하게 되면 Key를 생성해서 return;
		System.out.println("feedId: " + feedId);
		System.out.println("feedBody: " + feedHTTPVO);

		String userName = jwtProvider.getUserNameFromRequest(request);
		feedService.insertTempFeed(feedId, feedHTTPVO, userName);
	}

}
