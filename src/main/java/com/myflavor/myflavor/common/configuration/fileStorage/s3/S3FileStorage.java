package com.myflavor.myflavor.common.configuration.fileStorage.s3;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorage;
import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorageResult;
import com.myflavor.myflavor.common.configuration.fileStorage.common.StorageConfig;
import com.myflavor.myflavor.domain.picture.DTO.Picture;

@Component
public class S3FileStorage implements FileStorage {

	StorageConfig config;

	public S3FileStorage(S3Config config) {
		this.config = config;
	}

	public String getRootDirectory() {
		return null;
	}

	@Override
	public FileStorageResult uploadFile(Picture picture, Path path) {
		return null;
	}

	@Override
	public Resource downloadFile(Path path) {
		return null;
	}

	@Override
	public void deleteFile() {

	}
}
