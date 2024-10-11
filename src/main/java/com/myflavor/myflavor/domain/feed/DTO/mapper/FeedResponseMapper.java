package com.myflavor.myflavor.domain.feed.DTO.mapper;

import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.MainFeedResponseDTO;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;

public class FeedResponseMapper {
	public MainFeedResponseDTO ToMainFeedResponseDTO(MainFeedDTO mainFeedDTO) {
		return MainFeedResponseDTO.builder()
			.id(mainFeedDTO.getId())
			.title(mainFeedDTO.getTitle())
			.feedPhoto(mainFeedDTO.getFeedPhoto())
			.content(mainFeedDTO.getContent())
			.restaurantId(mainFeedDTO.getRestaurantId())
			.visitMethod(mainFeedDTO.getVisitMethod())
			.updatedAt(mainFeedDTO.getUpdatedAt())
			.heartCnt(mainFeedDTO.getHeartCnt())
			.build();
	}

	public FeedResponseDTO toFeedResponseDTO(MainFeed mainFeed) {

		// return FeedResponseDTO.builder()
		// 	.feedSetting(FeedSetting.builder().settings(mainFeed.getConfigurations()))
		// 	.build();

		return null;

	}

}
