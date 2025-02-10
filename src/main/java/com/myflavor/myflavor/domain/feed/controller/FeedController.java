package com.myflavor.myflavor.domain.feed.controller;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.CustomPageResponse;
import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.MainFeedResponseDTO;
import com.myflavor.myflavor.domain.feed.service.FeedService;

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
	public ResponseEntity<FeedResponseDTO> getFeed(@RequestParam("id") long feedId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		FeedResponseDTO feedResponseDTO = feedService.getFeed(feedId, userName);

		return ResponseEntity.ok()
			.body(feedResponseDTO);
	}

	@GetMapping("/list")
	public ResponseEntity<CustomPageResponse<MainFeedResponseDTO>> getFeeds(@RequestParam("p") int page,
		@RequestParam("s") int size,
		HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);

		Pageable pageable = PageRequest.of(page, size);
		CustomPageResponse<MainFeedResponseDTO> mainFeedPage = feedService.getFeedList(userName, pageable);

		return ResponseEntity.ok()
			.body(mainFeedPage);
	}

	@PostMapping
	public ResponseEntity<?> postFeed(@RequestParam(value = "id") long feedId, @RequestBody FeedResquestDTO feedHTTPVO,
		HttpServletRequest request, HttpServletResponse response) {
		// TODO postHTTPDTO를 분리하기 위해서 VO를 새로이 만든다.

		String userName = jwtProvider.getUserNameFromRequest(request);
		feedService.insertUpdateFeed(feedId, userName, feedHTTPVO);

		return ResponseEntity.ok().build();
	}

	@PatchMapping
	public void patchFeed(@RequestParam(value = "id") long feedId, @RequestBody FeedResquestDTO feedHTTPVO) {

	}

	@DeleteMapping
	public void deleteFeed(@RequestParam(value = "id") long feedId) {
	}

	@GetMapping("/key")
	public ResponseEntity<Long> getFeedKey() {
		// 새로운 feed를 만들기 위한 Feed Id를 발급
		// 해당 Feed Id를 활용하여 mysql, redis => 공통적으로 사용???
		long id = feedService.generateId();
		System.out.println(id);

		return ResponseEntity.ok().body(id);
	}

	// 유저가 임시 저장한 Feed의 목록을 가져 온다.
	@GetMapping("/temp/ids")
	public ResponseEntity<Set<Object>> getTempFeedIds(HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		return ResponseEntity.ok()
			.body(feedService.getTempFeedIds(userName));
	}

	@GetMapping("/temp")
	public ResponseEntity<FeedResquestDTO> getTempFeed(@RequestParam("id") long feedId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);

		return ResponseEntity.ok()
			.body(feedService.getTempFeed(feedId, userName));
	}

	@PostMapping("/temp")
	public ResponseEntity<?> insertTempFeed(@RequestParam(value = "id") long feedId,
		@RequestBody FeedResquestDTO feedHTTPVO,
		HttpServletRequest request) {
		// TODO: 최초 생성을 하게 되면 Key를 생성해서 return;
		System.out.println("feedId: " + feedId);
		System.out.println("feedBody: " + feedHTTPVO);

		String userName = jwtProvider.getUserNameFromRequest(request);
		feedService.insertTempFeed(feedId, feedHTTPVO, userName);
		return ResponseEntity.ok().build();
	}

}
