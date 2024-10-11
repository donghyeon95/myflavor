package com.myflavor.myflavor.common.configuration.SecurityConfig.oauth;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class OAuthAuthentication extends AbstractAuthenticationToken {

	private JwtProvider jwtProvider;

	public OAuthAuthentication(JwtProvider jwtProvider, Collection<? extends GrantedAuthority> authorities,
		HttpServletRequest req, HttpServletResponse res) {
		super(authorities);

		this.jwtProvider = jwtProvider;
		if (this.isOAuth2Authenticated(req, res)) {
			System.out.println("Authentication IN");
			super.setAuthenticated(true);  // 이게 Authentication을 해주는 Code
		}
	}

	public boolean isOAuth2Authenticated(HttpServletRequest req, HttpServletResponse res) {
		// JWT를 읽어서 인증된 사용자 인지 확인.
		Cookie[] cookies = req.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(jwtProvider.getACCESSTOKEN_NAME())) {
				return jwtProvider.isValid(cookie.getValue());
			}
		}

		return false;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}
}
