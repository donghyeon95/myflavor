package com.myflavor.myflavor.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.myflavor.myflavor.common.snowflakeId.SnowFlakeIdProvider;

@Configuration
public class UniqueIdConfig {

	@Bean
	public SnowFlakeIdProvider snowFlakeIdGen() {
		// worker의 경우는 docker node? 대충 그런 느낌으로 .....
		return new SnowFlakeIdProvider(1, 1);
	}
}
