package com.myflavor.myflavor.domain.feed.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDTO {
	private String content;
	private String picturePath;
}
