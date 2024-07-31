package com.myflavor.myflavor.domain.feed.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.domain.feed.model.DTO.CommentDTO;
import com.myflavor.myflavor.domain.feed.model.DTO.CommentHTTPVO;
import com.myflavor.myflavor.domain.feed.model.model.CommentTag;
import com.myflavor.myflavor.domain.feed.model.model.Comment;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;
import com.myflavor.myflavor.domain.feed.model.repository.CommentRepository;
import com.myflavor.myflavor.domain.feed.model.repository.CommentTagRepository;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;

@Service
public class CommentService {

	public CommentRepository commentRepository;
	public MainFeedRepository mainFeedRepository;
	public CommentTagRepository commentTagRepository;
	public UserRepository userRepository;

	public CommentService(CommentRepository commentRepository, MainFeedRepository mainFeedRepository,
			UserRepository userRepository, CommentTagRepository commentTagRepository) {
		this.commentRepository = commentRepository;
		this.mainFeedRepository = mainFeedRepository;
		this.userRepository = userRepository;
		this.commentTagRepository = commentTagRepository;
	}

	public List<Comment> getFeedComment(long feedId) {
		MainFeed mainFeed = mainFeedRepository.findById(feedId).orElseThrow();
		List<Comment> comments = commentRepository.findCommentsByMainFeed(mainFeed);

		List<CommentDTO> commentDTOs = comments.stream()
				.filter(Objects::nonNull)
				.map(this::commentDTOMapper)
				.toList();
		System.out.println(commentDTOs);
		return null;
	}

	public List<Comment> getCommentComment(long commentId) {
		Comment parentCommet = commentRepository.findById(commentId).orElseThrow();
		List<Comment> comments = commentRepository.findCommentsByParentComment(parentCommet);

		List<CommentDTO> commentDTOs = comments.stream()
				.filter(Objects::nonNull)
				.map(this::commentDTOMapper)
				.toList();
		System.out.println(commentDTOs);
		return null;
	}

	@Transactional
	public void insertFeedComment(long feedId, CommentHTTPVO commentHTTPVO, String userName) {
		MainFeed mainFeed = mainFeedRepository.findById(feedId).orElseThrow();
		User user = userRepository.findByName(userName).orElseThrow();
		List<String> tags = commentHTTPVO.getTags();

		// TODO 알림 서비스
		// TODO AOP 기반 Comment 허용여부 검사

		Comment comment = Comment.builder()
				.mainFeed(mainFeed)
				.comment(commentHTTPVO.getComment())
				.user(user)
				.build();

		commentRepository.save(comment);

		comment.setTags(tags.stream()
				.map(this::getUser)
				.filter(Objects::nonNull)
				.map(u -> CommentTag.builder()
						.comment(comment)
						.user(u)
						.build())
				.collect(Collectors.toList()));

		commentTagRepository.saveAll(comment.getTags());
	}

	@Transactional
	public void insertCommentComment(long commentId, CommentHTTPVO commentHTTPVO, String userName) {
		Comment rootComment = commentRepository.findById(commentId).orElseThrow();
		User user = userRepository.findByName(userName).orElseThrow();
		List<String> tags = commentHTTPVO.getTags();

		// TODO 알림 서비스

		Comment comment = Comment.builder()
				.parentComment(rootComment)
				.comment(commentHTTPVO.getComment())
				.user(user)
				.build();

		commentRepository.save(comment);

		List<CommentTag> commentTagsList = tags.stream()
				.map(this::getUser)
				.filter(Objects::nonNull) // null이 아닌 사용자만 처리
				.map(u -> CommentTag.builder()
						.comment(comment)
						.user(u)
						.build())
				.collect(Collectors.toList());

		commentTagRepository.saveAll(commentTagsList);
		// Comment 객체에 CommentTag 리스트 설정
		comment.setTags(commentTagsList);
		commentRepository.save(comment); // 다시 저장하여 관계 설정

	}

	@Transactional
	public void deleteComment(long commentId, String userName) {
		User user = userRepository.findByName(userName).orElseThrow();
		commentRepository.deleteAllByIdAndUser(commentId, user);
	}

	private User getUser(String tag) {
		return userRepository.findByName(tag.replace("@", "")).orElse(null);
	}

	private CommentDTO commentDTOMapper(Comment c) {
		return CommentDTO.builder()
				.id(c.getId())
				.comment(c.getComment())
				.comments(c.getComments())
				.user(c.getUser())
				.createdAt(c.getCreatedAt())
				.updatedAt(c.getUpdatedAt())
				.heartCnt(c.getHeartCnt())
				.tags(c.getTags())
				.build();
	}

}