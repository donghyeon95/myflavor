package com.myflavor.myflavor.domain.feed.DTO.response;

import java.time.LocalDateTime;

import com.myflavor.myflavor.domain.feed.DTO.service.VisitMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainFeedResponseDTO {
	private Long id;
	private LocalDateTime updatedAt;
	private String title;
	private String feedPhoto;
	private VisitMethod visitMethod;
	private String content;
	private Long restaurantId;
	private Integer heartCnt;
}
