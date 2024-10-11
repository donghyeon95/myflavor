package com.myflavor.myflavor.common.configuration.fileStorage.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;

import com.myflavor.myflavor.domain.picture.DTO.Picture;

public interface FileStorage {
	String getRootDirectory();

	FileStorageResult uploadFile(Picture picture, Path path) throws IOException;

	Resource downloadFile(Path path) throws MalformedURLException;

	void deleteFile();
}
