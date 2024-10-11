package com.myflavor.myflavor.common.provider.JWT;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Component
@Getter
public class JwtProvider {

	private final String ACCESSTOKEN_NAME = "access_token";
	private final String REFRESHTOKEN_NAME = "refresh_token";
	private final String USER_NAME_TOKEN_ID = "userName";

	private SecretKey secretKey;
	private String tokenHeader;
	private long expirationTimeMillis;

	public JwtProvider(
		@Value("${jwt.secretKey}") String secretKey,
		@Value("${jwt.header}") String header,
		@Value("${jwt.expirationTimeMillis}") long expirationTimeMillis
	) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		this.tokenHeader = header;
		this.expirationTimeMillis = expirationTimeMillis;
	}

	public String createToken(Map<String, Object> payload) {
		LocalDateTime currenTime = LocalDateTime.now();
		LocalDateTime expirationTime = currenTime.plusNanos(this.expirationTimeMillis * 10000);
		Date expireDate = Date.from(expirationTime.toInstant(ZoneOffset.ofHours(9)));

		return Jwts.builder()
			.subject("Authority")

			.issuedAt(new Date())
			.expiration(expireDate)

			.claims(payload)

			.signWith(this.secretKey)

			.compact();
	}

	public String createToken(Map<String, Object> header, Map<String, Object> payload) {
		LocalDateTime currenTime = LocalDateTime.now();
		LocalDateTime expirationTime = currenTime.plusNanos(this.expirationTimeMillis);
		Date expireDate = Date.from(expirationTime.toInstant(ZoneOffset.ofHours(9)));

		return Jwts.builder()

			.header()
			.add(header)
			.and()

			.subject("Authority")
			.issuedAt(new Date())
			.expiration(expireDate)

			.claims(payload)

			.signWith(this.secretKey)
			.compact();
	}

	public String getTokenHeader(String jwtToken) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.tokenHeader);
		sb.append(jwtToken);

		return sb.toString();
	}

	public boolean isValid(String jws) {
		// check valid Sign
		boolean result = isValidSign(jws);

		// check check expireTime
		result = result && isExpiration(jws);

		// check BlackList

		return result;
	}

	public boolean isValidSign(String jws) {
		try {
			Claims claims = this.getClaimFromToken(jws);

			return true;
		} catch (JwtException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	private boolean isExpiration(String jws) {
		try {
			final Date expiration = getExpriationFromToken(jws);
			if (expiration.before(new Date())) {
				System.out.println("Time OUT");
				return false;
			}
			System.out.println("time IN");
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public Date getExpriationFromToken(String jws) throws Exception {
		Date expiration = null;

		Claims claims = this.getClaimFromToken(jws);
		if (claims == null)
			throw new Exception();

		expiration = claims.getExpiration();
		return expiration;
	}

	public Claims getClaimFromToken(String jws) {
		Claims claims = null;

		try {
			claims = Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(jws).getPayload();
		} catch (JwtException e) {
			System.out.println(e.getMessage());
		}

		return claims;
	}

	public Header getHeaderFromToken(String jws) {
		Header header = null;

		try {
			header = Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(jws).getHeader();
		} catch (JwtException e) {
			System.out.println(e.getMessage());
		}

		return header;
	}

	public Claims getClaimsFromRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		Cookie accessTokenCookie = Arrays.stream(cookies)
			.filter(data -> data.getName().equals("access_token"))
			.findFirst()
			.orElseThrow();

		String accessToken = accessTokenCookie.getValue();

		return this.getClaimFromToken(accessToken);
	}

	public String getUserNameFromRequest(HttpServletRequest request) {
		return getClaimsFromRequest(request).get(this.USER_NAME_TOKEN_ID, String.class);
	}
}

// Chaining으로 만들어 보기
// Valid                                                                                                                                                Builder.
