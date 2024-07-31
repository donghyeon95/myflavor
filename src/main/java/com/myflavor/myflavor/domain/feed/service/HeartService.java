package com.myflavor.myflavor.domain.feed.service;

import org.springframework.stereotype.Service;

import com.myflavor.myflavor.domain.feed.model.repository.CommentRepository;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;

@Service
public class HeartService {
	private CommentRepository commentRepository;
	private MainFeedRepository mainFeedRepository;

	public HeartService(CommentRepository commentRepository, MainFeedRepository mainFeedRepository) {
		this.commentRepository = commentRepository;
		this.mainFeedRepository = mainFeedRepository;
	}

}
