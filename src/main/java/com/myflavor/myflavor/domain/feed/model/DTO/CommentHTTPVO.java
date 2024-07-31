package com.myflavor.myflavor.domain.feed.model.DTO;

import java.util.List;

import lombok.Data;

@Data
public class CommentHTTPVO {
	private String comment;
	private List<String> tags;
}
