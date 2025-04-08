package com.myflavor.myflavor.domain.account.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDTO {
	@Email
	@NotBlank
	@Size(max = 50, message = "50자 이하만 가능합니다.")
	private String userEmail;

	// @Email
	@NotBlank
	@Size(min = 8, max = 20, message = "8자 이상, 20자 이하만 가능합니다.")
	private String password;
}