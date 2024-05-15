package com.myflavor.myflavor.domain.account.adapter.service;

import com.myflavor.myflavor.domain.account.model.model.Account;
import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.account.model.repository.AccountRepository;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 전략 패턴과 팩토리 사용해서 Social Domain Service 로직을 변경할 수 있도록.

@Service
public class SignUpService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private SocialDomainFactory socialDomainFactory;


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
