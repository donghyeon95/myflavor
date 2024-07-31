package com.myflavor.myflavor.domain.feed.model.DTO;

import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Picture {
	private String id;
	private String fileName;
	@JsonIgnore
	private MultipartFile file;
	private String filePath;

	public Picture(MultipartFile file) {
		// this.id =
		this.fileName = file.getOriginalFilename();
		// FIND 이게 뭐지?
		this.id = UUID.randomUUID().toString();
		this.file = file;
	}

	public Picture setFilePath(String path) {
		if (this.filePath == null)
			this.filePath = path;
		else
			this.filePath = this.filePath + "/" + path;

		return this;
	}

}
