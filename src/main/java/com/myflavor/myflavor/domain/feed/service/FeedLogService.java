package com.myflavor.myflavor.domain.feed.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedLogService {
	private final RedisTemplate<String, Object> redisTemplate;

	// redis feed category Log service
	public void logFeedCategory() {

	}

	// redis feed user Log service

	// rdb Log Service

}
