package com.myflavor.myflavor.domain.account.adapter.service;


import com.myflavor.myflavor.domain.account.model.DTO.UserDTO;
import com.myflavor.myflavor.domain.account.model.repository.AccountRepository;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.common.JWT.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocialLoginService {

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


  public UserDTO.LoginResponseDTO socialLogin(String code, String socialDomain) throws Exception {
    UserDTO.SocialUserDTO socialUserDTO = socialDomainFactory.create(socialDomain).login(code, socialDomain);

    if (socialUserDTO == null) {
      return null;
    }

    return UserDTO.LoginResponseDTO.builder()
        .userEmail(socialUserDTO.getUserEmail())
        .userName(socialUserDTO.getUserName())
        .build();
  }

  public void setCookie(HttpServletRequest request, HttpServletResponse response) {
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
    System.out.println("발급된 토큰: " +jwt);
    Cookie cookie = new Cookie(this.ACCESSTOKEN_NAME, jwt);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  private boolean isSignup(String email) {
    return userRepository.findByUserEmail(email).orElse(null) != null;
  }
}

class TestException extends Exception {

  TestException(String message) {
    super(message);
  }
}
