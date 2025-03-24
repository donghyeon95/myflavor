package com.myflavor.myflavor.domain.feed.DTO.db;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDTO {
	@NotBlank(message = "컨텐츠는 필수 입력사항입니다.")
	private String content;
	private String picturePath;

	public FeedDTO(SubFeedDTO subFeedDTO) {
		this.content = subFeedDTO.getContent();
		this.picturePath = subFeedDTO.getFeedPhoto();
	}
}
