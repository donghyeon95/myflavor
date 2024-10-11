package com.myflavor.myflavor.common.configuration.fileStorage.AOP;

import java.nio.file.Path;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.myflavor.myflavor.common.configuration.fileStorage.common.StorageConfig;
import com.myflavor.myflavor.common.configuration.fileStorage.local.LocalConfig;
import com.myflavor.myflavor.common.configuration.fileStorage.s3.S3Config;
import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.picture.DTO.Picture;

@Aspect
@Component
public class FileCheckAop {

	private final LocalConfig localConfig;
	private final S3Config s3Config;

	public FileCheckAop(LocalConfig localConfig, S3Config s3Config) {
		this.localConfig = localConfig;
		this.s3Config = s3Config;
	}

	// 포인트 컷을 등록: 기준 지점을 등록 (Aspect가 적용될 수 있는 시점)
	// 여기서는 annotaion @FileAuth를 기준으로 사용
	@Pointcut("@annotation(com.myflavor.myflavor.common.configuration.fileStorage.AOP.FileAuth)")
	private void fileAuthCheck() {
	}

	@Pointcut("@annotation(fileTypeCheck)")
	private void fileTypeCheckPointcut(FileTypeCheck fileTypeCheck) {
	}

	// fileAuthCheck 포인트 컷이 시작되기 전에
	@Before("fileAuthCheck()")
	public void checkAuth(JoinPoint joinPoint) {

		Object[] args = joinPoint.getArgs();

		// 메소드로 들어오는 매개 변수들을 받는다.
		// User, path 정보를 활용하여 해당 path에 crud할 권한이 있는 지 확인을 한다.
		// 들어오는 객체를 통일을 하는 것이 좋을 듯 하다.
		User user = null;
		Path path = null;

		// 파일에 대한 권한이 있는 지 확인

	}

	@Before("fileTypeCheckPointcut(fileTypeCheck)")
	public void checkType(JoinPoint joinPoint, FileTypeCheck fileTypeCheck) {
		Picture picture = (Picture)Arrays.stream(joinPoint.getArgs())
			.filter(Picture.class::isInstance)
			.findFirst()
			.orElseThrow();

		FileStorageEnum storageType = fileTypeCheck.storageType();
		StorageConfig config = null;
		switch (storageType) {
			case S3 -> {
				config = s3Config;
				break;
			}
			case LOCAL -> {
				config = localConfig;
				break;
			}
			default -> throw new IllegalArgumentException("Invalid Storage Type");

		}

		String fileType = picture.getFile().getContentType();
		assert fileType != null;
		if (!config.getAllowedTypes().contains(fileType))
			throw new IllegalArgumentException("Invalid file type: " + fileType);
	}

}
