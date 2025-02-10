package com.myflavor.myflavor.domain.comment.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.comment.DTO.request.CommentRequestDTO;
import com.myflavor.myflavor.domain.comment.DTO.response.CommentGetResponseDTO;
import com.myflavor.myflavor.domain.comment.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
	public ResponseEntity<List<CommentGetResponseDTO>> getFeedComments(@RequestParam("id") long feedId) {
		List<CommentGetResponseDTO> commentGetResponseDTOs = commentService.getFeedComment(feedId);

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(commentGetResponseDTOs);
	}

	@GetMapping("/comment")
	// TODO Valid를 어떻게 할 지 고민을 해봐야 한다.
	public ResponseEntity<List<CommentGetResponseDTO>> getCommentComments(@RequestParam("id") long commentId) {
		List<CommentGetResponseDTO> commentGetReposneDTOs = commentService.getCommentComment(commentId);

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(commentGetReposneDTOs);
	}

	@PostMapping("/feed")
	public ResponseEntity<?> postFeedComments(@RequestParam("id") long feedId,
		@Valid @RequestBody CommentRequestDTO commentRequestDTO,
		HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.insertFeedComment(feedId, commentRequestDTO.reqToService(), userName);

		return ResponseEntity.ok()
			.body("");
	}

	@PostMapping("/comment")
	public ResponseEntity<?> postCommentComments(@RequestParam("id") long commentId,
		@Valid @RequestBody CommentRequestDTO commentRequestDTO,
		HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.insertCommentComment(commentId, commentRequestDTO.reqToService(), userName);

		return ResponseEntity.ok()
			.body("");
	}

	@DeleteMapping
	public ResponseEntity<?> deleteComment(@RequestParam("id") long commentId, HttpServletRequest request) {
		String userName = jwtProvider.getUserNameFromRequest(request);
		commentService.deleteComment(commentId, userName);

		return ResponseEntity.ok()
			.body("");
	}
}
