package com.myflavor.myflavor.domain.feed.DTO.request;

import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.service.FeedSetting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedResquestDTO {
	@NotBlank(message = "제목은 필수 입력값입니다.")
	@Size(max = 100, message = "제목은 최대 100자까지 입력가능 합니다.")
	private String title;

	@NotNull(message = "잘못된 입력입니다.")
	@Size(min = 1, message = "피드는 1개 이상 포함되어 있어야 합니다.")
	@Valid
	private FeedDTO[] feed;

	@Builder.Default
	private FeedSetting feedSetting = new FeedSetting();

	@NotBlank(message = "레스토랑 Id는 필수 입력값입니다.")
	private long restaurantId;

	@NotBlank(message = "유저 이름은 필수 입력값입니다.")
	private String userName;

	// INFO 이거 생성자 넣는 순서가 중요하다.
	public FeedResquestDTO(String title, FeedDTO[] feed, FeedSetting feedSetting, long restaurantId,
		String userName) {
		this.title = title;
		this.feed = feed;
		this.feedSetting = feedSetting != null ? new FeedSetting(feedSetting.getSettings()) : new FeedSetting();
		this.restaurantId = restaurantId;
		this.userName = userName;
	}
}
