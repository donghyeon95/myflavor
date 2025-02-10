package com.myflavor.myflavor.domain.feed.DTO.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDTO {
	private String content;
	private String picturePath;

	public FeedDTO(SubFeedDTO subFeedDTO) {
		this.content = subFeedDTO.getContent();
		this.picturePath = subFeedDTO.getFeedPhoto();
	}
}
