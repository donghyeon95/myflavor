package com.myflavor.myflavor.domain.comment.DTO.mapper;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.comment.DTO.db.CommentDTO;
import com.myflavor.myflavor.domain.comment.DTO.response.CommentGetResponseDTO;
import com.myflavor.myflavor.domain.comment.DTO.response.CommentUserResponseDTO;

public class CommentMapper {

	public CommentGetResponseDTO toCommentGetResponseDTO(CommentDTO commentDTO) {
		return CommentGetResponseDTO.builder()
			.id(commentDTO.getId())
			.updatedAt(commentDTO.getUpdatedAt())
			.heartCnt(commentDTO.getHeartCnt())
			.createdAt(commentDTO.getCreatedAt())
			.comment(commentDTO.getComment())
			.user(toCommentUserResponseDTO(commentDTO.getUser()))
			.build();
	}

	public CommentUserResponseDTO toCommentUserResponseDTO(User user) {
		return CommentUserResponseDTO.builder()
			.id(user.getId())
			.name(user.getName())
			.userEmail(user.getUserEmail())
			.build();
	}
}
