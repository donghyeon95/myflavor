package com.myflavor.myflavor.domain.account.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflavor.myflavor.common.exception.AccountException;
import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.account.DTO.request.UserSignUpRequestDTO;
import com.myflavor.myflavor.domain.account.DTO.service.UserServiceDTO;
import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class IntegrationAccountService {
	private final String ACCESSTOKEN_NAME = "access_token";
	private final String REFRESHTOKEN_NAME = "refresh_token";

	private JwtProvider jwtProvider;
	private UserRepository userRepository;

	public IntegrationAccountService(JwtProvider jwtProvider, UserRepository userRepository) {
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	public UserServiceDTO signIn(UserSignUpRequestDTO userSignUpRequestDTO) throws AccountException {
		String userEmail = userSignUpRequestDTO.getUserEmail();
		String password = userSignUpRequestDTO.getPassword();
		User user = userRepository.findByUserEmail(userEmail)
			.orElseThrow(() -> new AccountException("Invalid UserName"));

		if (!password.equals(user.getPassword())) {
			throw new AccountException("Invalid Password");
		}

		return UserServiceDTO.builder()
			.userName(user.getName())
			.userEmail(user.getUserEmail())
			.build();
	}

	public void setCookie(UserServiceDTO userServiceDTO, HttpServletRequest request, HttpServletResponse response) {
		// Cookie[] cookies = request.getCookies();
		// for (Cookie cookie : cookies) {
		// 	if (!ACCESSTOKEN_NAME.equals(cookie.getName()))
		// 		continue;
		// 	/*이미 쿠키가 있을 경우
		// 	 * 해당 쿠키를  확인 후 재발급
		// 	 * JWT TOKEN 형태면 token 만료 기간 확인 후 시간 남았으면 Black List에 집어 넣기
		// 	 * */
		// 	String token = cookie.getValue();
		// }

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> payload = objectMapper.convertValue(userServiceDTO,
			new TypeReference<Map<String, Object>>() {
			});

		String jwt = jwtProvider.createToken(payload);
		System.out.println("발급된 토큰: " + jwt);
		Cookie cookie = new Cookie(this.ACCESSTOKEN_NAME, jwt);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

}
