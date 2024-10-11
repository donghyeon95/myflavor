package com.myflavor.myflavor.domain.comment.DTO.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentGetResponseDTO {
	private long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String comment;
	private Integer heartCnt;
	private CommentUserResponseDTO user;
}
