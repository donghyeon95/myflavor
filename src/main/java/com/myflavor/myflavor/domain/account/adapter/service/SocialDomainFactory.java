package com.myflavor.myflavor.domain.account.adapter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.myflavor.myflavor.domain.account.model.DTO.UserDTO;
import com.myflavor.myflavor.domain.account.model.model.Account;
import com.myflavor.myflavor.domain.account.model.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class SocialDomainFactory {
//  private HashMap<String, DomainCreator<SocialDomainStrategy>> domainMap = new HashMap<>();
//
//  // 이걸 조금 더 세련되게 바꿀 수는 없을까?
//  // 또한 이렇게 bean으로 등록하는게 맞나? 걍 static으로 하는 게 좋지 않을까?
//  @Bean
//  public HashMap<String, DomainCreator<SocialDomainStrategy>> init(){
//    domainMap.put("google", Google::new);
//    domainMap.put("naver", Naver::new);
//    return domainMap;
//  }


  private static ApplicationContext context;
  private static HashMap<String, DomainCreator<Object>> domainMap = new HashMap<>();

  public SocialDomainFactory(ApplicationContext context) {
    this.context = context;
    domainMap.put("google", context::getBean);
    domainMap.put("naver", context::getBean);
  }


//  static {
////    domainMap.put("google", Google::new);
////    domainMap.put("naver", Naver::new);
//  }

  public <c extends  SocialDomainStrategy> c  create(String domain){
     DomainCreator socialDomain = domainMap.get(domain);
     if (socialDomain == null) {
      throw new IllegalArgumentException("지원되지 않는 도메인입니다 domian: " + domain);
     }
     return (c)socialDomain.create(domain);
   }
}


//@FunctionalInterface
//interface DomainCreator<T> {
//  T create();
//}

@FunctionalInterface
interface DomainCreator<T> {
  T create(String domain);
}


@Component(value = "google")
class Google implements SocialDomainStrategy {
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private Environment env;
  @Autowired
  private AccountRepository accountRepository;


  @Override
  public boolean login() {
    return true;
  }

  public UserDTO.SocialUserDTO login(String code, String socialDomain) throws Exception{
    JsonNode tokenNode = getAccessToken(code, socialDomain);
    JsonNode userResourceNode = getUserResource(tokenNode, socialDomain);

    if(!this.isLogin(tokenNode, userResourceNode)) {
      return null;
    }

    return UserDTO.SocialUserDTO.builder()
        .userName(userResourceNode.get("name").asText())
        .userEmail(userResourceNode.get("email").asText())
        .build();
  }


  @Transactional
  public boolean isLogin(JsonNode tokenNode, JsonNode userResourceNode) throws Exception{

    // 해당 요청에 accessToken 쿠키가 있을 경우 accessToken과 그것과 연관된 refreshToken은 Black List로 간다.
    // 또는 토큰이 있을 때, 로그인을 하게 되면 이미 로그인한 유저 입니다. -> at 토큰의 유효 하지 않다면 refresh Token을 통해 새로운 aT 발급 -> rt도 유효하지 않다면 로그아웃으로 리다이렉트

    String userEmail = String.valueOf(userResourceNode.get("email").asText());
    System.out.println(userEmail);
    Account account = accountRepository.findByUserEmail(userEmail)
        .orElse(null);


    if (account == null) {
      // 없는 아이디라면 회원가입을 하도록
      return false;
    }

    // Account 테이블에서 AccessToken과 RefreshToken을 수정한다.
    // TODO 여기서 null일 경우에 대한 세련된 처리
    JsonNode accessToken = tokenNode.get("access_token");
    JsonNode refreshToken = tokenNode.get("refresh_token");

    if (accessToken != null) {
      account.setAccessToken(accessToken.asText());
    }
    if (refreshToken != null) {
      account.setRefreshToken(refreshToken.asText());
    }
//    String idToken = tokenNode.get("id_token").asText();

    // 만약 다른 정보에도 변경사항이 있다면 변경해준다.(정보를 가지고 있을 필요가 있나?)
    accountRepository.save(account);

    return true;
  }


  private JsonNode getAccessToken(String code, String socialDomain) {
    // google에 요청을 보내서 accessToken과 refreshToekn을 받아온다.
    String clientId = env.getProperty("oauth." + socialDomain + ".clientId");
    String clientSecret = env.getProperty("oauth." + socialDomain + ".clientSecret");
    String redirectUri  = env.getProperty("oauth." + socialDomain + ".redirectUri");
    String tokenUri = env.getProperty("oauth." + socialDomain + ".tokenUri");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity entity = new HttpEntity(params, headers);


    ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
    JsonNode tokenNode = responseNode.getBody();
    return tokenNode;
  }

  private JsonNode getUserResource(JsonNode tokenNode, String socialDomain) {
    String accessToken = tokenNode.get("access_token").asText();
    String resourceUri = env.getProperty("oauth."+socialDomain+".resourceUri");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity entity = new HttpEntity(headers);

    return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
  }

  public boolean signUp(){
    System.out.println("google 회원가입 입니다.");
    return true;

  }

  public boolean logout(){
    System.out.println("google 로그아웃 입니다.");
    return true;
  }

  public boolean hasRegister(){
    return true;
  }
}



@Component(value = "naver")
class Naver implements SocialDomainStrategy {
  public boolean login(){
    System.out.println("naver 로그인 입니다.");
    return true;
  }

  @Deprecated
  public UserDTO.SocialUserDTO login(String code, String SocialDomain){return new UserDTO.SocialUserDTO();}


  public boolean signUp(){
    System.out.println("naver 회원가입 입니다.");
    return true;
  }

  public boolean logout(){
    System.out.println("naver 로그아웃 입니다.");
    return true;
  }

  public boolean hasRegister(){
    return true;
  }
}

@Component
interface SocialDomainStrategy {
  boolean login();

  UserDTO.SocialUserDTO login(String code, String SocialDomain) throws  Exception;

  boolean signUp();

  boolean logout();

  boolean hasRegister();
}