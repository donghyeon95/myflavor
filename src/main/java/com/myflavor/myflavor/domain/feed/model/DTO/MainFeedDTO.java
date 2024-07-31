package com.myflavor.myflavor.domain.feed.model.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
}
