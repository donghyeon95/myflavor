package com.myflavor.myflavor.domain.profile.dto;

import com.myflavor.myflavor.domain.profile.model.entity.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FollowDTO {
	private String followerName;
	private String followingName;

	public FollowDTO(Follow follow) {
		followerName = follow.getFollower().getName();
		followingName = follow.getFollowing().getName();
	}
}
