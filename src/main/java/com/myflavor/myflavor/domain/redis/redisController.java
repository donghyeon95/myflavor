package com.myflavor.myflavor.domain.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflavor.myflavor.domain.redis.model.RedisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/redis", produces = "application/json")
public class redisController {
  @Autowired
  RedisService redisService;
  @Autowired
  ObjectMapper objectMapper;

  @PostMapping("/insert")
  public void insert(@RequestBody RedisDTO obj) throws IOException {
    String mappperv = objectMapper.writeValueAsString((Object) obj);
    redisService.insert("hi", mappperv);
    RedisDTO redisData = (RedisDTO) redisService.get("hi", RedisDTO.class);
    System.out.println(redisData.getUser());
  }
}
