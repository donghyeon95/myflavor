// package com.myflavor.myflavor.common.configuration.log;
//
// import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import ch.qos.logback.classic.AsyncAppender;
// import ch.qos.logback.classic.Level;
// import ch.qos.logback.classic.LoggerContext;
// import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
// import ch.qos.logback.classic.spi.ILoggingEvent;
// import ch.qos.logback.core.ConsoleAppender;
//
// @Configuration
// public class LogbackConfig {
//
// 	@Bean
// 	public LoggerContext loggerContext() {
// 		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
//
// 		// 패턴 인코더 설정
// 		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
// 		encoder.setContext(loggerContext);
// 		encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{transactionId}] %logger{36} - %msg%n");
// 		encoder.start();
//
// 		// ConsoleAppender 설정
// 		ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
// 		consoleAppender.setContext(loggerContext);
// 		consoleAppender.setEncoder(encoder);
// 		consoleAppender.start();
//
// 		// 비동기 Appender 설정
// 		AsyncAppender asyncAppender = new AsyncAppender();
// 		asyncAppender.setContext(loggerContext);
// 		asyncAppender.addAppender(consoleAppender);
// 		asyncAppender.setQueueSize(512);  // 큐의 크기 설정
// 		asyncAppender.start();
//
// 		// 루트 로거 설정
// 		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
// 		rootLogger.setLevel(Level.INFO);
// 		rootLogger.addAppender(asyncAppender);
//
// 		// Hibernate SQL 로거 설정
// 		ch.qos.logback.classic.Logger sqlLogger = loggerContext.getLogger("org.hibernate.SQL");
// 		sqlLogger.setLevel(Level.DEBUG);
// 		sqlLogger.addAppender(asyncAppender);
//
// 		// Hibernate 파라미터 바인딩 로거 설정
// 		ch.qos.logback.classic.Logger binderLogger = loggerContext.getLogger(
// 			"org.hibernate.type.descriptor.sql.BasicBinder");
// 		binderLogger.setLevel(Level.TRACE);
// 		binderLogger.addAppender(asyncAppender);
//
// 		return loggerContext;
// 	}
// }
