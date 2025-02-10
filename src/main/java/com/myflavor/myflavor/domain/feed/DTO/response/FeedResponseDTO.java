package com.myflavor.myflavor.domain.feed.DTO.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.service.FeedSetting;
import com.myflavor.myflavor.domain.feed.DTO.service.VisitMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponseDTO {
	private String title;
	private LocalDateTime updatedAt;
	private VisitMethod visitMethod;
	private int heartCnt;
	// private Restaurant restaurant;
	private String userName;
	private List<FeedDTO> feeds;
	private FeedSetting feedSetting;

	public FeedResponseDTO(MainFeedDTO mainFeedDTO) {
		this.title = mainFeedDTO.getTitle();
		this.feedSetting = new FeedSetting();
		mainFeedDTO.getConfigration().stream()
			.filter(Objects::nonNull)
			.forEach(this.feedSetting::add);
		this.visitMethod = mainFeedDTO.getVisitMethod();
		this.heartCnt = mainFeedDTO.getHeartCnt();
		this.updatedAt = mainFeedDTO.getUpdatedAt();
		this.userName = mainFeedDTO.getUser() != null ? mainFeedDTO.getUser().getName() : null;
		this.feeds = new ArrayList<>();
		this.feeds.add(new FeedDTO(mainFeedDTO.getContent(), mainFeedDTO.getFeedPhoto()));
		this.feeds.addAll(mainFeedDTO.getSubFeeds().stream().filter(Objects::nonNull).map(FeedDTO::new).toList());
	}
}
