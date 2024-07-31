package com.myflavor.myflavor.common.configuration;

import com.myflavor.myflavor.common.JWT.JwtProvider;
import com.myflavor.myflavor.common.configuration.SecurityConfig.oauth.CustomOauth2Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConifg {
	// Security Filter chain 전체를 구성
	@Autowired
	private JwtProvider jwtProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable) // csrf(csrf -> csrf.disable())
				.sessionManagement(
						sessionCreationPolicy -> sessionCreationPolicy.sessionCreationPolicy(SessionCreationPolicy.NEVER))
				// .authorizeHttpRequests(authorizedHttpRequests -> authorizedHttpRequests.requestMatchers("/signup/sociallogin/**").permitAll())
				// .authorizeHttpRequests(
				// 		// 해당 httpServletRequest에 Session이 있는 지 확인.
				// 		// authorizedHttpRequests -> authorizedHttpRequests
				// 		// 		.requestMatchers("/signup/sociallogin/**").permitAll()
				// 		// 		.requestMatchers("/signup/login").permitAll()
				// 		// .anyRequest().permitAll()
				// 		// .anyRequest().authenticated()
				// // )
				.addFilterAfter(new CustomOauth2Filter(jwtProvider), BasicAuthenticationFilter.class)
				.build();
	}

	// JWT TokenFilter를 무시하기 위해 아래 API는 filter 자체를 무시
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers("/signup/login");
	}

	//  @Bean
	//  public SecurityFilterChain filterChain2(HttpSecurity http) throws  Exception {
	//
	//  }

}

/**
 class filter extends GenericFilter {

@Override public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

}
}*/