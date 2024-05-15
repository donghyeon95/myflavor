package com.myflavor.myflavor.domain.account.model.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Entity
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
  public class Account {

  @Id
  @GeneratedValue(strategy =  GenerationType.AUTO)
  private long id;

  @Column(name = "domain")
  @Enumerated(EnumType.STRING)
  private SocialDomain socialDomain;

  @NotNull
  @NotBlank
  @Email(message = "잘못된 이메일 형식입니다.")
  private String userEmail;

  private String familyName;

  private String givenName;

  private String code;

  private String accessToken;

  private String refreshToken;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", socialDomain=" + socialDomain +
        ", userEmail='" + userEmail + '\'' +
        ", familyName='" + familyName + '\'' +
        ", givenName='" + givenName + '\'' +
        ", code='" + code + '\'' +
        ", accessToken='" + accessToken + '\'' +
        ", refreshToken='" + refreshToken + '\'' +
        ", user=" + user +
        '}';
  }
}
