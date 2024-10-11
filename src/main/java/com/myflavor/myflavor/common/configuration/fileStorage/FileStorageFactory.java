package com.myflavor.myflavor.common.configuration.fileStorage;

import org.springframework.stereotype.Component;

import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorage;
import com.myflavor.myflavor.common.configuration.fileStorage.local.LocalFileStroge;
import com.myflavor.myflavor.common.configuration.fileStorage.s3.S3FileStorage;

@Component
public class FileStorageFactory {

	private final LocalFileStroge localFileStroge;
	private final S3FileStorage s3FileStorage;

	public FileStorageFactory(LocalFileStroge localFileStroge, S3FileStorage s3FileStorage) {
		this.localFileStroge = localFileStroge;
		this.s3FileStorage = s3FileStorage;
	}

	public FileStorage getFileStorage(FileStorageType fileStorgeType) throws Exception {
		if (fileStorgeType == FileStorageType.S3) {
			return s3FileStorage;
		} else if (fileStorgeType == FileStorageType.LOCAL) {
			return localFileStroge;
		} else {
			throw new Exception("error");
		}
	}

}