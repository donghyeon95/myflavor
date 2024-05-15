package com.myflavor.myflavor.domain.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  public void insert(String key, String redisData) {
    final ValueOperations<String, Object> stringStringValueOperations = redisTemplate.opsForValue();
    stringStringValueOperations.set(key, redisData);
  }


  public Object get(String key, Class<?> type) throws IOException {
    final ValueOperations<String, Object> stringStringValueOperations = redisTemplate.opsForValue();
    String data = (String) stringStringValueOperations.get(key);

    return objectMapper.readValue(data, type);
  }

}
