package com.myflavor.myflavor.common.configuration.fileStorage.common;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.myflavor.myflavor.domain.picture.DTO.Picture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileStorageResult {
	// File Storage 결과
	private String status;

	// 업로드 항목
	private List<Path> upload;

	// 수정 항목
	private List<Path> update;

	//삭제 항목
	private List<Path> deleted;

	// request 항목 반환
	private List<Picture> requestFiles;

	public FileStorageResult() {
		this.update = new ArrayList<>();
		this.upload = new ArrayList<>();
		this.deleted = new ArrayList<>();
		this.requestFiles = null;
	}

	public FileStorageResult(List<Picture> request) {
		this();
		this.requestFiles = request;
	}

	public void addUpload(Path path) {
		this.upload.add(path);
	}

	public void addDelete(Path path) {
		this.deleted.add(path);
	}

	public void addUpdate(Path path) {
		this.update.add(path);
	}
}
