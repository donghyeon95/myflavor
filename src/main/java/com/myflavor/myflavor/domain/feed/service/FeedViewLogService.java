package com.myflavor.myflavor.domain.feed.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FeedViewLogService {
	private final String USER_FEED_VIEW_LOG = "user:feed_view:";
	private final String CATEGORY_SUFFIX = ":category";
	private final String WRITER_SUFFIX = ":writer";

	private RedisTemplate<String, Object> redisTemplate;

	public FeedViewLogService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * user별 자주 본 카테고리 상위 count개 조회
	 */
	public List<String> getFrequentViewCategory(String userName, int count) {
		String frequentPersonkey = USER_FEED_VIEW_LOG + userName + WRITER_SUFFIX;

		return Objects.requireNonNull(redisTemplate.opsForZSet().reverseRange(
				frequentPersonkey, 0, count - 1))
			.stream().map(String::valueOf).collect(
				Collectors.toList());
	}

	/**
	 * user 별 자주 본 유저
	 */
	public List<String> getFrequentViewWriter(String userName, int count) {
		String frequentCategoryKey = USER_FEED_VIEW_LOG + userName + CATEGORY_SUFFIX;

		return Objects.requireNonNull(
				redisTemplate.opsForZSet().reverseRange(frequentCategoryKey, 0, count - 1))
			.stream().map(String::valueOf).collect(Collectors.toList());
	}

}
