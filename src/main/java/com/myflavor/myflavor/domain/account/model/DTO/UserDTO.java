package com.myflavor.myflavor.domain.account.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


public class UserDTO {
  @Getter
  @Builder
  public static class UserSignupRequest {
    @Email
    @NotBlank
    @Size(max=50, message = "50자 이하만 가능합니다.")
    private String userEmail;

    @Email
    @NotBlank
    @Size(min=8, max=20, message = "8자 이상, 20자 이하만 가능합니다.")
    private String password;
  }

  @Builder
  @Getter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  public static  class LoginResponseDTO {
    private String sessionId;
    private String userName;
    private String userEmail;
  }

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SocialUserDTO {
    private String sessionId;
    private String userName;
    private String userEmail;
  }
}
