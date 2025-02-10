package com.myflavor.myflavor.common.configuration.fileStorage.AOP;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileTypeCheck {
	FileStorageEnum storageType();
}
