package com.myflavor.myflavor.common.configuration;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Configuration
public class TransactionLoggingAspect {

	@Around("@annotation(transactional)")
	public Object aroundTransaction(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
		String transactionId = UUID.randomUUID().toString();
		MDC.put("transactionId", transactionId);

		try {
			// 트랜잭션 메서드 실행
			return joinPoint.proceed();
		} finally {
			// 트랜잭션 완료 후 MDC에서 트랜잭션 ID 제거
			MDC.remove("transactionId");
		}
	}
}