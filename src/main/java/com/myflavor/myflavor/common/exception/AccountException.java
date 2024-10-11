package com.myflavor.myflavor.common.exception;

import lombok.Getter;

@Getter
public class AccountException extends CustomRuntimeException {
	private int statusCode; //Http 상태 코드
	private String errorCode; // 사용자 정의 에러 코드
	private String errorMessage;
	
	public AccountException() {
		this.statusCode = 400;
		this.errorCode = "Account Error";
		this.errorMessage = "Invalid Account Error";
	}

	public AccountException(String message) {
		this();
		this.errorMessage = message;
	}
}
