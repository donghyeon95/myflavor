package com.myflavor.myflavor.common.configuration.fileStorage.local;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.myflavor.myflavor.common.configuration.fileStorage.common.StorageConfig;

import lombok.Data;

@Data
@Component
public class LocalConfig extends StorageConfig {
	@Value("${application.file-path}")
	private String rootPath;
	@Value("${application.file.allowed-extension}")
	private String allowedTypes;

	public LocalConfig() {

	}

	public LocalConfig(String rootPath, String allowedTypes) {
		this.rootPath = rootPath;
		this.allowedTypes = allowedTypes;
	}
}