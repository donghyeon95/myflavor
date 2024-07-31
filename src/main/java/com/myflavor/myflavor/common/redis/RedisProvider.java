package com.myflavor.myflavor.common.redis;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Component
public class RedisProvider {

	private RedisMessageListenerContainer container;
	private volatile int subscriberCnt = 0;

	public RedisProvider(RedisMessageListenerContainer container) {
		this.container = container;
	}

	@Service
	private static class Subscriber {

	}

	private class Publisher {
	}

}
