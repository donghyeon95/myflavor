package com.myflavor.myflavor.domain.comment.DTO.service;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentServiceDTO {
	private String comment;
	private List<String> tags;
}
