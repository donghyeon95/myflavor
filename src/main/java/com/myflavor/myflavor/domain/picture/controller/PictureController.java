package com.myflavor.myflavor.domain.picture.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.picture.DTO.Picture;
import com.myflavor.myflavor.domain.picture.service.PictureService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/picture")
public class PictureController {

	private PictureService pictureService;
	private JwtProvider jwtProvider;

	public PictureController(PictureService pictureService, JwtProvider jwtProvider) {
		this.pictureService = pictureService;
		this.jwtProvider = jwtProvider;
	}

	@GetMapping("/download/{userName}/{feedId}/{priority}/{fileName}")
	public ResponseEntity<Resource> getPicture(@PathVariable String userName,
		@PathVariable long feedId,
		@PathVariable String fileName,
		@PathVariable int priority,
		HttpServletRequest request,
		HttpServletResponse response) {

		System.out.println("userName " + userName);
		// userName 권한이 있는지 확인
		if (!pictureService.hasAccess(request, userName)) {
			System.out.println(userName);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Resource resource = null;

		try {
			resource = pictureService.getFile(userName, feedId, priority, fileName);
		} catch (MalformedURLException e) {
			return ResponseEntity.status(404).build();
		}

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			contentType = "application/octet-stream";
		}

		String encodedFileName;
		try {
			encodedFileName = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8.toString());
		} catch (IOException e) {
			encodedFileName = Base64.getEncoder()
				.encodeToString(resource.getFilename().getBytes(StandardCharsets.UTF_8));
			encodedFileName = "=?UTF-8?B?" + encodedFileName + "?=";
		}

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
			.body(resource);
	}

	@PostMapping("/upload")
	public Picture postPicture(@RequestParam("id") long feedId, @RequestParam("pr") int priority,
		@RequestParam("p") MultipartFile pictureFile,
		HttpServletRequest request) throws
		IOException {
		// TODO 일단 사진은 저장을 하고
		// TODO 만약 최종 저장을 하지 않는 다면 지우도록(배치로 처리, timer)
		String userName = jwtProvider.getUserNameFromRequest(request);
		Picture picture = pictureService.saveFile(new Picture(pictureFile), userName, feedId,
			priority);
		return picture;
	}
}
