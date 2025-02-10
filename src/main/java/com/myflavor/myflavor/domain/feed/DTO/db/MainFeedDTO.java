package com.myflavor.myflavor.domain.feed.DTO.db;

import java.time.LocalDateTime;
import java.util.List;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.feed.DTO.service.VisitMethod;
import com.myflavor.myflavor.domain.feed.model.entity.FeedConfigration;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainFeedDTO {
	private Long id;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String title;
	private String feedPhoto;
	private VisitMethod visitMethod;
	private String content;
	private Long restaurantId;
	private Integer heartCnt;
	private User user;
	private List<FeedConfigration> configration;
	private List<SubFeedDTO> subFeeds;

	// TODO 요 부분에서 collection fetch Join 더 자연 스럽게 ....
	public MainFeedDTO(MainFeed mainFeed) {
		this.id = mainFeed.getId();
		this.createdAt = mainFeed.getCreatedAt();
		this.updatedAt = mainFeed.getUpdatedAt();
		this.title = mainFeed.getTitle();
		this.feedPhoto = mainFeed.getFeedPhoto();
		this.visitMethod = mainFeed.getVisitMethod();
		this.content = mainFeed.getContent();
		this.restaurantId = mainFeed.getRestaurantId();
		this.heartCnt = mainFeed.getHeartCnt();
		this.configration = mainFeed.getConfigurations();
		this.user = mainFeed.getUser();
		this.subFeeds = mainFeed.getSubFeeds()
			.stream()
			.sorted((d1, d2) -> (d1.getPriority() >= d2.getPriority()) ? 1 : -1)
			.map(SubFeedDTO::new)
			.toList();
	}
}
