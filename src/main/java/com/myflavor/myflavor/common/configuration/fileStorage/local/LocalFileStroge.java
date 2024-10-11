package com.myflavor.myflavor.common.configuration.fileStorage.local;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.myflavor.myflavor.common.configuration.fileStorage.AOP.FileStorageEnum;
import com.myflavor.myflavor.common.configuration.fileStorage.AOP.FileTypeCheck;
import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorage;
import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorageResult;
import com.myflavor.myflavor.domain.picture.DTO.Picture;

@Component
public class LocalFileStroge implements FileStorage {

	private LocalConfig config;

	public LocalFileStroge(LocalConfig localConfig) {
		this.config = localConfig;
	}

	@Override
	public String getRootDirectory() {
		return this.config.getRootPath();
	}

	@Override
	@FileTypeCheck(storageType = FileStorageEnum.LOCAL)
	// TODO AOP로 File에 대한 보안 검사(ex. fileType, 위변조 검사)
	public FileStorageResult uploadFile(Picture picture, Path path) throws IOException {
		List<Picture> request = new ArrayList<>();
		request.add(picture);

		FileStorageResult result = new FileStorageResult(request);

		Path parentDir = path.getParent();
		// 파일 이름 변경
		String sanitizedFileName = picture.getFileName().replaceAll("[^\\p{IsAlphabetic}\\d.\\-_]", "_");
		picture.setFileName(sanitizedFileName);
		Path filepath = path.resolve(sanitizedFileName);
		picture.setFilePath(filepath.toString());

		// 디렉토리가 없다면 만들기
		this.makeParentDir(path);

		// 이미 있는 file일 경우에는 삭제 처리
		result.setDeleted(this.delteExistFile(path).getDeleted());

		// 새로운 file 저장
		result.addUpload(this.save(filepath, picture.getFile()));

		return result;
	}

	@Override
	public Resource downloadFile(Path filePath) throws MalformedURLException {
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists()) {
			return resource;
		} else {
			throw new RuntimeException("File not found " + filePath);
		}
	}

	@Override
	public void deleteFile() {

	}

	public Path save(Path filePath, MultipartFile file) throws IOException {
		System.out.println("Uplaod File Path: " + filePath);
		file.transferTo(filePath);
		return filePath;
	}

	public void makeParentDir(Path parentDir) throws IOException {
		if (!Files.exists(parentDir))
			Files.createDirectories(parentDir);
	}

	public FileStorageResult delteExistFile(Path parentDir) throws IOException {
		FileStorageResult result = new FileStorageResult();

		// try-with-resource로 stream File 자원 반환
		try (Stream<Path> paths = Files.list(parentDir)) {
			paths.forEach(path -> {
				int retryCount = 0;
				while (retryCount < 3) {
					try {
						// 파일 삭제 성공
						Files.delete(path);
						// 삭제 성공한 File을 저장.
						result.addDelete(path);
						System.out.println("Deleted: " + path);
						break;
					} catch (IOException e) {
						// 파일 삭제 실패 시 재시도
						retryCount++;
						System.out.println("Failed to delete " + path + "_" + e.getMessage());
					}
				}
			});

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

}
