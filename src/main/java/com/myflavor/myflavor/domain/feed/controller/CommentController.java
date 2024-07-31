package com.myflavor.myflavor.domain.feed.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.JWT.JwtProvider;
import com.myflavor.myflavor.domain.feed.model.DTO.CommentHTTPVO;
import com.myflavor.myflavor.domain.feed.model.model.Comment;
import com.myflavor.myflavor.domain.feed.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/comment", produces = "application/json")
public class CommentController {

	private CommentService commentService;
	private JwtProvider jwtProvider;

	public CommentController(CommentService commentService, JwtProvider jwtProvider) {
		this.commentService = commentService;
		this.jwtProvider = jwtProvider;
	}

	@GetMapping("/feed")
	// TODO Valid를 어떻게 할 지 고민을 해봐야 한다.
	public List<Comment> getFeedComments(@RequestParam("id") long feedId) {
		commentService.getFeedComment(feedId);
		return null;
	}

	@GetMapping("/comment")
	// TODO Valid를 어떻게 할 지 고민을 해봐야 한다.
	public List<Comment> getCommentComments(@RequestParam("id") long commentId) {
		commentService.getCommentComment(commentId);
		return null;
	}

	@PostMapping("/feed")
	public void postFeedComments(@RequestParam("id") long feedId, @RequestBody CommentHTTPVO commentHTTPVO,
			HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.insertFeedComment(feedId, commentHTTPVO, userName);
	}

	@PostMapping("/comment")
	public void postCommentComments(@RequestParam("id") long commentId, @RequestBody CommentHTTPVO commentHTTPVO,
			HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.insertCommentComment(commentId, commentHTTPVO, userName);
	}

	@DeleteMapping
	public void deleteComment(@RequestParam("id") long commentId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.deleteComment(commentId, userName);
	}
}
