package com.myflavor.myflavor.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${application.file-path}")
	private String filePath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String formattedFilePath = "file:" + filePath.replace("\\", "/") + "/";

		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(formattedFilePath)
				.setCachePeriod(3600)
				.resourceChain(true)
				.addResolver(new PathResourceResolver());
	}
}
