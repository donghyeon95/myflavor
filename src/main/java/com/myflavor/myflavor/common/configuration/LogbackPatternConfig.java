package com.myflavor.myflavor.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.PatternLayout;

@Configuration
public class LogbackPatternConfig {
	@Bean
	public PatternLayout patternLayout() {
		PatternLayout layout = new PatternLayout();
		layout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{transactionId}] %logger{36} - %msg%n");
		return layout;
	}
}
