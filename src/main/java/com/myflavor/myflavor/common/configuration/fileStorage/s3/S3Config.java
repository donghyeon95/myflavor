package com.myflavor.myflavor.common.configuration.fileStorage.s3;

import org.springframework.stereotype.Component;

import com.myflavor.myflavor.common.configuration.fileStorage.common.StorageConfig;

import lombok.Data;

@Data
@Component
public class S3Config extends StorageConfig {
	private String s3Region;
	private String bucketName;

}