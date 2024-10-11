package com.myflavor.myflavor.domain.comment.DTO.request;

import java.util.List;

import com.myflavor.myflavor.domain.comment.DTO.service.CommentServiceDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequestDTO {
	@NotNull
	private String comment;

	private List<@NotNull String> tags;

	public CommentServiceDTO reqToService() {
		return CommentServiceDTO.builder()
			.comment(comment)
			.tags(tags)
			.build();
	}

}
