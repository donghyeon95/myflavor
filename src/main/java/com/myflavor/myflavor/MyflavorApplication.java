package com.myflavor.myflavor;

import com.myflavor.myflavor.common.Envconfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@PropertySource(value = {
    "classpath:env.yml",
    }, factory = Envconfig.class)
public class MyflavorApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyflavorApplication.class, args);
  }
}
