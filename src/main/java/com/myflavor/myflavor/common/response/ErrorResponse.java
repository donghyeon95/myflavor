package com.myflavor.myflavor.common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	private int status;
	private String errorCode;
	private String message;

	public ErrorResponse(int status) {
		this.status = status;
		this.errorCode = null;
		this.message = null;
	}

	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.errorCode = null;
	}

	public ErrorResponse(int status, String errorCode, String message) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
	}
}
