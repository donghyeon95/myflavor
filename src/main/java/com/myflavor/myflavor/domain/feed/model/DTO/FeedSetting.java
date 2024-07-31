package com.myflavor.myflavor.domain.feed.model.DTO;

import java.util.HashMap;
import java.util.Map;

import io.lettuce.core.ScriptOutputType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class FeedSetting {
	@Builder.Default
	private Map<SettingKey, String> settings = new HashMap<>();

	public FeedSetting() {
		System.out.println("feedKEySDKLfj");
		this.settings = new HashMap<>();
		for (SettingKey s : SettingKey.values()) {
			this.settings.put(s, s.getDefaultValue());
		}
	}

	public FeedSetting(Map<SettingKey, String> settingKeys) {
		this.settings = new HashMap<>();
		for (SettingKey s : settingKeys.keySet()) {
			String value = settingKeys.get(s);
			if (!s.isValidValue(value))
				throw new IllegalArgumentException("Invalid value for setting key " + s);

			this.settings.put(s, value);
		}

		System.out.println("SettigKEY" + SettingKey.values());
		for (SettingKey s : SettingKey.values()) {
			System.out.println("동작");
			this.settings.putIfAbsent(s, s.getDefaultValue());
		}
	}
}
