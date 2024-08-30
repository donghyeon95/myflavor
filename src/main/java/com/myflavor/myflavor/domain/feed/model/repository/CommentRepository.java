package com.myflavor.myflavor.domain.feed.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.model.Comment;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("select c from Comment c where c.mainFeed=:mainFeed")
	List<Comment> findCommentsByMainFeed(MainFeed mainFeed);

	@Query("select c from Comment c where c.parentComment=:comment")
	List<Comment> findCommentsByParentComment(Comment comment);

	void deleteAllByIdAndUser(long commentId, User user);
}
