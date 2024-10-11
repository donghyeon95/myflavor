package com.myflavor.myflavor.domain.feed.DTO.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
import com.myflavor.myflavor.domain.feed.DTO.service.FeedSetting;
import com.myflavor.myflavor.domain.feed.DTO.service.SettingKey;
import com.myflavor.myflavor.domain.feed.model.entity.FeedConfigration;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;

import lombok.AllArgsConstructor;
import lombok.Data;

public class FeedMapper {

	public static FeedEntities feedMapper(long feedId, FeedResquestDTO feedHTTPVO, User user) {
		String title = feedHTTPVO.getTitle();
		FeedDTO[] feeds = feedHTTPVO.getFeed();
		FeedSetting feedSetting = feedHTTPVO.getFeedSetting();

		if (feeds == null || feeds.length == 0)
			throw new NoSuchElementException("Invalid");

		List<SubFeed> subFeeds = new ArrayList<>();
		for (int i = 1; i < feeds.length; i++) {
			FeedDTO feedDTO = feeds[i];
			SubFeed subFeed = SubFeed.builder()
				.priority(i)
				.feedPhoto(feedDTO.getPicturePath())
				.content(feedDTO.getContent())
				.build();

			subFeeds.add(subFeed);
		}

		FeedDTO mainFeedDTO = feeds[0];
		MainFeed mainFeed = MainFeed.builder()
			.id(feedId)
			.title(title)
			.feedPhoto(mainFeedDTO.getPicturePath())
			.content(mainFeedDTO.getContent())
			.configurations(feedConfigrationMapper(feedSetting))
			.user(user)
			.subFeeds(subFeeds)
			.build();

		return new FeedEntities(mainFeed, subFeeds);
	}

	public static List<FeedConfigration> feedConfigrationMapper(FeedSetting feedSetting) {
		List<FeedConfigration> configrations = new ArrayList<>();
		Map<SettingKey, String> settings = feedSetting.getSettings();

		settings.forEach((settingKey, value) -> {
			System.out.println(settingKey + " : " + value);
			configrations.add(FeedConfigration.builder()
				.settingkey(settingKey)
				.settingValue(value)
				.build());
		});

		return configrations;
	}

	@Data
	@AllArgsConstructor
	public static class FeedEntities {
		private MainFeed mainFeed;
		private List<SubFeed> subFeeds;
	}
}


