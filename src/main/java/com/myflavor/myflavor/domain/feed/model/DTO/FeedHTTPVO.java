package com.myflavor.myflavor.domain.feed.model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class FeedHTTPVO {
	private String title;
	private FeedDTO[] feed;
	@Builder.Default
	private FeedSetting feedSetting = new FeedSetting();
	private Restaurant restaurant;
	private String userName;

	// INFO 이거 생성자 넣는 순서가 중요하다.
	public FeedHTTPVO(String title, FeedDTO[] feed, FeedSetting feedSetting, Restaurant restaurant, String userName) {
		this.title = title;
		this.feed = feed;
		this.feedSetting = feedSetting != null ? new FeedSetting(feedSetting.getSettings()) : new FeedSetting();
		this.restaurant = restaurant;
		this.userName = userName;
	}
}
