package com.myflavor.myflavor.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

@Configuration
public class WebClientConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
