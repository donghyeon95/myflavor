package com.myflavor.myflavor.domain.account.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.domain.account.DTO.service.SocialUserServiceDTO;
import com.myflavor.myflavor.domain.account.service.SocialSignUpService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/socialaccount")
public class SocialAccountController {

	private SocialSignUpService socialSignUpService;

	public SocialAccountController(SocialSignUpService socialSignUpService) {
		this.socialSignUpService = socialSignUpService;
	}

	@GetMapping("/login/{socialDomain}")
	public ResponseEntity<?> socialLogin(@RequestParam String code, @PathVariable String socialDomain,
		HttpServletResponse response, HttpServletRequest request) throws Exception {
		// Controller Service 로직
		SocialUserServiceDTO loginResponseDTO = socialSignUpService.socialLogin(code, socialDomain);
		System.out.println(loginResponseDTO.toString());

		if (loginResponseDTO == null) {
			// Redis에 Data를 넣어두고 socialSignUp 주소를 보내도록.
			// forward로 변경하는 게 좋지 않을 까?
			String redirect_url = String.format("http://localhost:8080/socialaccount/social/%s", socialDomain);

			return ResponseEntity.badRequest()
				.header("Location", redirect_url)
				.contentType(MediaType.APPLICATION_JSON)
				.body(loginResponseDTO);

		} else {
			socialSignUpService.setCookie(response);
			String redirect_url = String.format("http://localhost:8080/socialaccount/social/%s", socialDomain);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.LOCATION, redirect_url);

			return ResponseEntity.status(HttpStatus.FOUND)
				//          .header("Location", redirect_url)
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(loginResponseDTO);
		}
	}

	@PostMapping("/signup/{socialDomain}")
	public void socialSignUp(@PathVariable String socialDomain, @RequestParam String sessionId,
		@RequestBody Object obj) {

		socialSignUpService.socialSignUp(null, null, socialDomain);
	}

	// 기존 아이디 소셜 연동하기
	@GetMapping("/link/{socialDomain}")
	public void linkSocialDomain(@RequestParam String code, @PathVariable String socialDomain) {
	}
}
