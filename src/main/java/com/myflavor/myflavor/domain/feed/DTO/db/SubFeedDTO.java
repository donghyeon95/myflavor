package com.myflavor.myflavor.domain.feed.DTO.db;

import java.time.LocalDateTime;

import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubFeedDTO {
	private long id;
	private LocalDateTime updatedAt;
	private int priority;
	private String feedPhoto;
	private String content;

	public SubFeedDTO(SubFeed subFeed) {
		this.id = subFeed.getId();
		this.updatedAt = subFeed.getUpdatedAt();
		this.priority = subFeed.getPriority();
		this.feedPhoto = subFeed.getFeedPhoto();
		this.content = subFeed.getContent();
	}
}
