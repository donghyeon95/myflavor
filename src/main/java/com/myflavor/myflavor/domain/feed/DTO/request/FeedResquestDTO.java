package com.myflavor.myflavor.domain.feed.DTO.request;

import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.service.FeedSetting;
import com.myflavor.myflavor.domain.restaurant.DTO.Restaurant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedResquestDTO {
	private String title;
	private FeedDTO[] feed;
	@Builder.Default
	private FeedSetting feedSetting = new FeedSetting();
	private Restaurant restaurant;
	private String userName;

	// INFO 이거 생성자 넣는 순서가 중요하다.
	public FeedResquestDTO(String title, FeedDTO[] feed, FeedSetting feedSetting, Restaurant restaurant,
		String userName) {
		this.title = title;
		this.feed = feed;
		this.feedSetting = feedSetting != null ? new FeedSetting(feedSetting.getSettings()) : new FeedSetting();
		this.restaurant = restaurant;
		this.userName = userName;
	}
}
