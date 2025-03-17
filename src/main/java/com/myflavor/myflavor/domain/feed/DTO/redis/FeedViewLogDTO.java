package com.myflavor.myflavor.domain.feed.DTO.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedViewLogDTO {
	private String viewerName;
	private long feedId;
	private String restaurantCategory;
	private String feedWriterName;
	private double latitude;
	private double longitude;
}
