package com.myflavor.myflavor.domain.profile.dto;

import org.springframework.data.domain.Page;

import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {

	long follwerCnt;
	long followingCnt;
	long feedCnt;

	Page<FeedResponseDTO> feeds;
}
