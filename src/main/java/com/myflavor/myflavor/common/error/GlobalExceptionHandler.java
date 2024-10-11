package com.myflavor.myflavor.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.myflavor.myflavor.common.exception.AccountException;
import com.myflavor.myflavor.common.response.ErrorResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountException.class)
	public ResponseEntity<ErrorResponse> handleGlobalAccountException(AccountException ex,
		HttpServletResponse response) {
		// TODO 쿠키 삭제 로직  이걸 여기서 하는 게 좋나?
		// 아니면 service Logic에서?
		// 쿠키 삭제 로직
		Cookie accessTokenCookie = new Cookie("access_token", null);  // 삭제할 쿠키 이름 지정
		accessTokenCookie.setMaxAge(0);  // 쿠키의 수명을 0으로 설정하여 삭제
		accessTokenCookie.setPath("/");  // 쿠키의 경로 설정
		response.addCookie(accessTokenCookie);

		return new ResponseEntity<>(new ErrorResponse(ex.getStatusCode(), ex.getErrorMessage()),
			HttpStatus.BAD_REQUEST);
	}
}

