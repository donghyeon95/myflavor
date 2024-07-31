package com.myflavor.myflavor.domain.feed.model.model;

import com.myflavor.myflavor.domain.feed.model.DTO.SettingKey;
import com.myflavor.myflavor.domain.feed.model.model.listener.FeedConfigurationListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(FeedConfigurationListener.class)
public class FeedConfigration {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	private SettingKey settingkey;
	private String settingValue;

	@ManyToOne
	private MainFeed mainFeedId;

	// public void setSettingValue(String settingValue) {
	// 	if (!settingkey.isValidValue(settingValue)) {
	// 		throw new IllegalArgumentException("Invalid value for setting key " + settingkey);
	// 	}
	// 	this.settingValue = settingValue;
	// }
}