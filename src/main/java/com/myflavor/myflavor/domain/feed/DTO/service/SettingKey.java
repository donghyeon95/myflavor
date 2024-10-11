package com.myflavor.myflavor.domain.feed.DTO.service;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum SettingKey {
	COMMNET(Arrays.asList("true", "false"), "true"),
	USERPERMIT(Arrays.asList("friend", "all", "closetFriend"), "all"),
	HEARTEXPOSURE(Arrays.asList("true", "false"), "true"),
	FEEDEXPOSURE(Arrays.asList("true", "false"), "true");

	private final List<String> allowedValues;
	private final String defaultValue;

	SettingKey(List<String> allowedValues, String defaultValue) {
		this.allowedValues = allowedValues;
		this.defaultValue = defaultValue;
	}

	public List<String> getAllowedValues() {
		return allowedValues;
	}

	public boolean isValidValue(String value) {
		return allowedValues.contains(value);
	}

}
