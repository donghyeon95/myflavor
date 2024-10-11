package com.myflavor.myflavor.common.configuration.fileStorage.common;

import lombok.Data;

@Data
public abstract class StorageConfig {
	private String rootPath;
	private String allowedTypes;
}
