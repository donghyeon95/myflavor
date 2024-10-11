package com.myflavor.myflavor.domain.account.DTO.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceDTO {
	private String sessionId;
	private String userName;
	private String userEmail;
}

