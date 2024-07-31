package com.myflavor.myflavor.domain.feed.model.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.model.Comment;
import com.myflavor.myflavor.domain.feed.model.model.CommentTag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentDTO {
	private long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String comment;
	private List<CommentTag> tags;
	private Integer heartCnt;
	private List<Comment> comments;
	private User user;
}
