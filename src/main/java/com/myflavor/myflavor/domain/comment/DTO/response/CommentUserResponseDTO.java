package com.myflavor.myflavor.domain.comment.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentUserResponseDTO {
	private long id;
	private String userEmail;
	private String name;
}
