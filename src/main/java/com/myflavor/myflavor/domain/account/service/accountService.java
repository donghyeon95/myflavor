package com.myflavor.myflavor.domain.account.service;

import com.myflavor.myflavor.domain.account.model.entity.Account;
import com.myflavor.myflavor.domain.account.model.entity.User;

import jakarta.servlet.http.HttpServletResponse;

// TODO 해당 interface를 활용해야 할까?
public interface accountService {

	// 회원 여부 확인
	public boolean isSignUp();

	// 로그인 여부 확인
	public boolean isSignIn();

	// 쿠키 세팅
	public void setCookie(HttpServletResponse response);

	// 로그인
	private void signIn() {
	}

	// 로그아웃
	public void signOut();

	// 회원가입
	public void signUp(Account account, User user, String domain);
}
