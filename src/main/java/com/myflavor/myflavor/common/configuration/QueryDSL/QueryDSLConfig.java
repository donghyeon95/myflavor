package com.myflavor.myflavor.common.configuration.QueryDSL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QueryDSLConfig {
	// 보기에는 entityManager 한개인 것으로 보이지만 실제로는 Factory에서 받아온다고 한다.
	@PersistenceContext
	private final EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}
