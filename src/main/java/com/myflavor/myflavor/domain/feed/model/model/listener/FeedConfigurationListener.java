package com.myflavor.myflavor.domain.feed.model.model.listener;

import com.myflavor.myflavor.domain.feed.model.model.FeedConfigration;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class FeedConfigurationListener {

	@PrePersist
	@PreUpdate
	public void validateFeedConfiguration(FeedConfigration feedConfigration) {
		if (!feedConfigration.getSettingkey().isValidValue(feedConfigration.getSettingValue())) {
			throw new IllegalArgumentException("Invalid value for setting key " + feedConfigration.getSettingkey());
		}
	}
}
