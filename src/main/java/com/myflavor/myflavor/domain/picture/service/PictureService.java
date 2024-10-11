package com.myflavor.myflavor.domain.picture.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.myflavor.myflavor.common.configuration.fileStorage.FileStorageFactory;
import com.myflavor.myflavor.common.configuration.fileStorage.FileStorageType;
import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorage;
import com.myflavor.myflavor.common.configuration.fileStorage.common.FileStorageResult;
import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.picture.DTO.Picture;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PictureService implements MessageListener {

	private final String REDIS_PICUTURE_KEY_PREFIX = "picture: ";

	@Value("${application.file-path}")
	private String UPLOAD_DIR;
	@Value("${application.domain}")
	private String DOMAIN;
	@Value("${application.file.allowed-extension}")
	private List<String> allowedFileTypes;

	private JwtProvider jwtProvider;
	private RedisTemplate<String, Object> redisTemplate;

	// file 관련 repository같은 Interface를 DI
	private FileStorage fileStorage;

	public PictureService(JwtProvider jwtProvider, RedisTemplate<String, Object> redisTemplate,
		FileStorageFactory fileStorageFactory) throws
		Exception {
		this.jwtProvider = jwtProvider;
		this.redisTemplate = redisTemplate;

		this.fileStorage = fileStorageFactory.getFileStorage(FileStorageType.LOCAL);
	}

	// TODO PICTURE FILE FACTORY을 DI 받을 수 있도록 수정 ( Interface로 만들어서 그냥 file => NAS or S3 가능하도록.)

	public boolean hasAccess(HttpServletRequest request, String userName) {
		return userName.equals(jwtProvider.getClaimsFromRequest(request).get("userName", String.class));
	}

	public Resource getFile(String userName, long feedId, int priority, String fileName) throws MalformedURLException {
		Path userDir = Paths.get(this.fileStorage.getRootDirectory(), userName).toAbsolutePath().normalize();
		Path feedDir = userDir.resolve(Long.toString(feedId));
		Path priorityDir = feedDir.resolve(Integer.toString(priority));
		Path filePath = priorityDir.resolve(fileName).normalize();

		return this.fileStorage.downloadFile(filePath);
	}

	public Picture saveFile(Picture picture, String userName, long feedId, int priority) throws IOException {
		Path userDir = Paths.get(this.UPLOAD_DIR, userName);
		Path feedDir = userDir.resolve(Long.toString(feedId));
		Path priorityDir = feedDir.resolve(Integer.toString(priority));

		// file upload
		FileStorageResult result = this.fileStorage.uploadFile(picture, priorityDir);
		List<Path> paths = result.getDeleted();

		// Redis update
		for (Path path : paths) {
			// reids에 저장된 Key를 삭제
			redisTemplate.delete(this.REDIS_PICUTURE_KEY_PREFIX + path);
		}

		String filepath = picture.getFilePath();
		String pictureKey = this.REDIS_PICUTURE_KEY_PREFIX + filepath;
		redisTemplate.opsForValue().set(pictureKey, "", 1, TimeUnit.DAYS);

		// picture filePath 업데이트
		picture.setFilePath(DOMAIN)
			.setFilePath("picture/download")
			.setFilePath(userName)
			.setFilePath(Long.toString(feedId))
			.setFilePath(Integer.toString(priority))
			.setFilePath(picture.getFileName());

		return picture;
	}

	public void deleteFile(String path, String userName) throws IllegalAccessException, IOException {
		if (path == null)
			throw new IllegalArgumentException("Path is nessesary");

		String[] splitedPath = path.split("/");
		if (splitedPath.length < 5)
			throw new IllegalArgumentException("Path is not valid");

		String user = splitedPath[splitedPath.length - 4];
		String feedId = splitedPath[splitedPath.length - 3];
		String priority = splitedPath[splitedPath.length - 2];
		String fileName = splitedPath[splitedPath.length - 1];

		if (!user.equals(userName)) {
			System.out.println("user: " + user + " userName: " + userName);
			throw new IllegalAccessException("Illegal user access ");
		}

		try {
			Path pictureUserPath = Paths.get(String.valueOf(this.UPLOAD_DIR), user);
			Path pictureFeedPath = pictureUserPath.resolve(feedId);
			Path picturePriorityPath = pictureFeedPath.resolve(priority);
			Path picturePath = picturePriorityPath.resolve(fileName);

			Files.delete(picturePath);
			System.out.println("File deleted successfully: " + picturePath);

			deleteEmptyDirectories(picturePriorityPath);
			deleteEmptyDirectories(pictureFeedPath);
			deleteEmptyDirectories(pictureUserPath);

		} catch (NoSuchFileException e) {
			System.err.println("No such file/directory exists: " + path);
			throw new NoSuchFileException("No such file/directory exists: " + path);
		} catch (IOException e) {
			System.err.println("Unable to delete file: " + path);
			throw new IOException("Unable to delete file: " + path, e);
		}
	}

	public void deleteFile(Path path) throws IllegalAccessException, IOException {
		if (path == null)
			throw new IllegalArgumentException("Path is nessesary");

		try {

			Files.delete(path);
			System.out.println("File deleted successfully: " + path);

			deleteEmptyDirectories(path.getParent(), 0);

		} catch (NoSuchFileException e) {
			System.err.println("No such file/directory exists: " + path);
			throw new NoSuchFileException("No such file/directory exists: " + path);
		} catch (IOException e) {
			System.err.println("Unable to delete file: " + path);
			throw new IOException("Unable to delete file: " + path, e);
		}
	}

	public void deleteEmptyDirectories(Path path) throws IOException {
		try {
			if (Files.isDirectory(path) && Files.list(path).findAny().isEmpty()) {
				Files.delete(path);
				System.out.println("Directory deleted successfully: " + path);
			}
		} catch (DirectoryNotEmptyException e) {

		}
	}

	public void deleteEmptyDirectories(Path path, int depth) throws IOException {
		try {
			if (Files.isDirectory(path) && Files.list(path).findAny().isEmpty()) {
				Files.delete(path);
				System.out.println("Directory deleted successfully: " + path);
				if (depth < 2) {
					System.out.println("isIN");
					deleteEmptyDirectories(path.getParent(), ++depth);
				} else {
					System.out.println("isOut");
				}
			} else {
				System.out.println("isININ");
			}
		} catch (DirectoryNotEmptyException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String redisKey = message.toString();
		String path = redisKey.replace(this.REDIS_PICUTURE_KEY_PREFIX, "");

		System.out.println("path: " + path);

		// FIXME 의존성 수정
		String user = extractFourthLastElementFromPath(path);

		if (redisKey.startsWith(this.REDIS_PICUTURE_KEY_PREFIX)) {
			try {
				deleteFile(Paths.get(path));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static String extractFourthLastElementFromPath(String filePath) {
		try {
			Path path = Paths.get(filePath);
			// 뒤에서 4번째 요소를 추출하기 위해 요소 개수를 확인
			int nameCount = path.getNameCount();
			if (nameCount < 4) {
				throw new IllegalArgumentException(
					"Path does not contain enough elements to extract the fourth last element.");
			}
			return path.getName(nameCount - 4).toString();
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException("Invalid file path: " + filePath, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error extracting element from path: " + e.getMessage(), e);
		}
	}

}
