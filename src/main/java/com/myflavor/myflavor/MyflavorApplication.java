package com.myflavor.myflavor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.myflavor.myflavor.common.configuration.enviroment.Envconfig;

@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@PropertySource(value = {
	"classpath:env.yml",
}, factory = Envconfig.class)
@EnableJpaAuditing
public class MyflavorApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(MyflavorApplication.class, args);

	}
}
