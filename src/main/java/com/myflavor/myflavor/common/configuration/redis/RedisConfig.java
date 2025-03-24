package com.myflavor.myflavor.common.configuration.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.myflavor.myflavor.domain.feed.service.FeedService;
import com.myflavor.myflavor.domain.picture.service.PictureService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {
	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	// ER
	@Bean
	public RedisTemplate<?, Object> defaultRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<?, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}

	// Redis template
	@Bean
	public RedisTemplate<?, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<?, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);   //connection
		redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
		// redisTemplate.setValueSerializer(new StringRedisSerializer());  // value
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // value
		// redisTemplate.setDefaultSerializer(RedisSerializer.string());
		redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return redisTemplate;
	}

	@Bean
	public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter feedListenerAdapter,
		MessageListenerAdapter pictureListenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		// 사용할 채널 토픽 설정
		// 여러 토픽일 경우 계속해서 등록하도록.
		container.addMessageListener(feedListenerAdapter, new PatternTopic("__keyevent@0__:expired"));
		container.addMessageListener(pictureListenerAdapter, new PatternTopic("__keyevent@0__:expired"));
		return container;
	}

	@Bean
	public MessageListenerAdapter feedListenerAdapter(@Lazy FeedService feedService) {
		return new MessageListenerAdapter(feedService, "onMessage"); // handleMessage로 안되나?
	}

	@Bean
	public MessageListenerAdapter pictureListenerAdapter(@Lazy PictureService pictureService) {
		return new MessageListenerAdapter(pictureService, "onMessage"); // handleMessage로 안되나?
	}

}
