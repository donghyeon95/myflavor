package com.myflavor.myflavor.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ApiErrorCode {
	// 공통 오류
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

	// 게시글 오류
	FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
	FEED_CREATION_FAILLED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글을 생성할 수 없습니다"),
	FEED_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글을 수정할 수 없습니다."),
	FEED_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글을 삭제할 수 없습니다."),

	// 댓글 오류
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
	COMMENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "댓글을 생성할 수 없습니다."),
	COMMENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "댓글을 삭제할 수 없습니다."),

	// 유저 오류
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다."),

	// 인증 오류
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

	// Validation 오류
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),

	// 좋아요 오류
	LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),
	LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 기록이 없습니다.");

	private final HttpStatus status;
	private final String message;

	ApiErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
