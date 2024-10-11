package com.myflavor.myflavor.domain.account.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.account.DTO.service.SocialUserServiceDTO;
import com.myflavor.myflavor.domain.account.model.entity.Account;
import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.account.model.repository.AccountRepository;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class SocialSignUpService {

	private final String ACCESSTOKEN_NAME = "access_token";
	private final String REFRESHTOKEN_NAME = "refresh_token";

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Environment env;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private SocialDomainFactory socialDomainFactory;

	public SocialUserServiceDTO socialLogin(String code, String socialDomain) throws Exception {
		SocialUserServiceDTO socialUserDTO = socialDomainFactory.create(socialDomain).login(code, socialDomain);

		if (socialUserDTO == null) {
			return null;
		}

		return SocialUserServiceDTO.builder()
			.userEmail(socialUserDTO.getUserEmail())
			.userName(socialUserDTO.getUserName())
			.build();
	}

	public void setCookie(HttpServletResponse response) {
		//    Cookie[] cookies = request.getCookies();
		//    for (Cookie cookie: cookies) {
		//      if (!ACCESSTOKEN_NAME.equals(cookie.getName())) continue;
		//      /*이미 쿠키가 있을 경우
		//      * 해당 쿠키를  확인 후 재발급
		//      * JWT TOKEN 형태면 token 만료 기간 확인 후 시간 남았으면 Black List에 집어 넣기
		//      * */
		//      String token = cookie.getValue();
		//    }

		Map<String, Object> payload = new HashMap<>();
		payload.put("userName", "donghyeon");

		String jwt = jwtProvider.createToken(payload);
		System.out.println("발급된 토큰: " + jwt);
		Cookie cookie = new Cookie(this.ACCESSTOKEN_NAME, jwt);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	private boolean isSignup(String email) {
		return userRepository.findByUserEmail(email).orElse(null) != null;
	}

	@Transactional
	public void socialSignUp(Account account, User user, String domain) {
		try {
			socialDomainFactory
				.create(domain)
				.signUp();
			// account 생성 시 전화번호 가져오기

			// 전화번호를 기준으로 User Table에서 해당 정보를 가진 유저가 있는 지 확인

			// 없다면 새로운 유저 생서 하는 값 Return

			accountRepository.save(account);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean hasUserByPhoneNumber(String PhoneNumber) {
		return true;
	}
}
