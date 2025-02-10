package com.myflavor.myflavor.common.configuration.SecurityConfig.oauth;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

//@Component
public class CustomOauth2Filter extends OncePerRequestFilter {

	//  @Autowired
	private JwtProvider jwtProvider;

	public CustomOauth2Filter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	// 로그인이 되어있는 지 여부 확인???
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws
		IOException,
		ServletException {
		System.out.println("첫번째 Custom filter");
		Collection<? extends GrantedAuthority> role = new ArrayList<>();

		//    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("userDetails", null, role);
		//    // RemoteAddress, SessionId를 가지고 오는 함수
		//    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

		OAuthAuthentication oAuthAuthentication = new OAuthAuthentication(jwtProvider, role, req, res);
		SecurityContextHolder.getContext().setAuthentication(oAuthAuthentication);

		System.out.println("ISIN?");
		// Filter의 다음 함수를 실행하는 함수
		chain.doFilter(req, res);
	}
}
