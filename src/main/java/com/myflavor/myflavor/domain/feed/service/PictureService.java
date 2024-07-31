package com.myflavor.myflavor.domain.feed.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.myflavor.myflavor.common.JWT.JwtProvider;
import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.DTO.Picture;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PictureService implements MessageListener {

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${application.file-path}")
	private String UPLOAD_DIR;

	@Value("${application.domain}")
	private String DOMAIN;

	private final String REDIS_PICUTURE_KEY_PREFIX = "picture: ";

	private List<String> allowedFileTypes = Arrays.asList("image/jpg", "image/jpeg", "image/png");

	public boolean hasAccess(HttpServletRequest request, String userName) {
		return userName.equals(jwtProvider.getClaimsFromRequest(request).get("userName", String.class));
	}

	public Resource getFile(String userName, long feedId, int priority, String fileName) throws MalformedURLException {
		Path userDir = Paths.get(this.UPLOAD_DIR, userName).toAbsolutePath().normalize();
		Path feedDir = userDir.resolve(Long.toString(feedId));
		Path priorityDir = feedDir.resolve(Integer.toString(priority));
		Path filePath = priorityDir.resolve(fileName).normalize();

		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists()) {
			return resource;
		} else {
			throw new RuntimeException("File not found " + fileName);
		}
	}

	public Picture saveFile(Picture picture, String userName, long feedId, int priority) throws IOException {

		// TODO FILE 검사
		// file 확장자 검사
		if (!allowedFileTypes.contains(picture.getFile().getContentType())) {
			throw new IllegalArgumentException("Invalid file type");
		}

		Path userDir = Paths.get(this.UPLOAD_DIR, userName);
		Path feedDir = userDir.resolve(Long.toString(feedId));
		Path priorityDir = feedDir.resolve(Integer.toString(priority));

		if (!Files.exists(priorityDir)) {
			Files.createDirectories(priorityDir);

		} else {
			Files.list(priorityDir).forEach(path -> {
				try {
					Files.delete(path);
					System.out.println("Deleted: " + path);

					// reids에 저장된 Key를 삭제
					redisTemplate.delete(this.REDIS_PICUTURE_KEY_PREFIX + path);
				} catch (NoSuchFileException e) {
					System.err.println("No such file/directory exists: " + path);
				} catch (DirectoryNotEmptyException e) {
					System.err.println("Directory is not empty: " + path);
				} catch (IOException e) {
					System.err.println("Unable to delete file: " + path);
				}
			});

		}

		try {
			// FIXME 파일 이름 변경 - 외국어 전부
			String sanitizedFileName = picture.getFileName().replaceAll("[^\\p{IsAlphabetic}\\d.\\-_]", "_");
			Path filepath = priorityDir.resolve(sanitizedFileName);
			System.out.println("filePath: " + filepath);

			picture.getFile().transferTo(filepath);
			System.out.println("success file input");

			// file 저장에 성공을 하면 redis에 ttl 등록
			String pictureKey = this.REDIS_PICUTURE_KEY_PREFIX + filepath;
			redisTemplate.opsForValue().set(pictureKey, "", 1, TimeUnit.DAYS);

			picture.setFilePath(DOMAIN)
					.setFilePath("picture/download")
					.setFilePath(userName)
					.setFilePath(Long.toString(feedId))
					.setFilePath(Integer.toString(priority))
					.setFilePath(sanitizedFileName);
		} catch (IOException e) {
			throw new IOException("Could not save File: " + picture.getFileName(), e);
		}

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
			Path pictureUserPath = Paths.get(this.UPLOAD_DIR, user);
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
				throw new IllegalArgumentException("Path does not contain enough elements to extract the fourth last element.");
			}
			return path.getName(nameCount - 4).toString();
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException("Invalid file path: " + filePath, e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error extracting element from path: " + e.getMessage(), e);
		}
	}

}
