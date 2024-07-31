package com.myflavor.myflavor.domain.account.adapter.controller;

import com.myflavor.myflavor.domain.account.adapter.service.SignUpService;
import com.myflavor.myflavor.domain.account.adapter.service.SocialLoginService;
import com.myflavor.myflavor.domain.account.model.DTO.UserDTO;
import com.myflavor.myflavor.common.JWT.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/signup", produces = "application/json")
public class SignUp {

	@Autowired
	private SocialLoginService socialLoginService;
	@Autowired
	private SignUpService signUpService;

	@Autowired
	private JwtProvider jwtProvider;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDTO.UserSignupRequest userDTO, HttpServletRequest request,
			HttpServletResponse response) {
		// login이 된다면 JWT Token을 넘겨줄 수 있도록 해야한다.
		// HttpSession httpSession = request.getSession();
		System.out.println("login 1");

		socialLoginService.setCookie(request, response);
		HttpHeaders headers = new HttpHeaders();

		System.out.println("login");
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body("");
	}

	@GetMapping("/login")
	public ResponseEntity<?> getlogin(@RequestBody UserDTO.UserSignupRequest userDTO, HttpServletRequest request,
			HttpServletResponse response) {
		// login이 된다면 JWT Token을 넘겨줄 수 있도록 해야한다.
		// HttpSession httpSession = request.getSession();
		System.out.println("login 1");

		socialLoginService.setCookie(request, response);
		HttpHeaders headers = new HttpHeaders();

		System.out.println("login");
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.build();
	}

	// 기존 아이디 소셜 연동하기
	@GetMapping("/link/{socialDomain}")
	public void linkSocialDomain(@RequestParam String code, @PathVariable String socialDomain) {
	}

	@GetMapping("/sociallogin/{socialDomain}")
	public ResponseEntity<?> socialLogin(@RequestParam String code, @PathVariable String socialDomain,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		// Controller Service 로직
		UserDTO.LoginResponseDTO loginResponseDTO = socialLoginService.socialLogin(code, socialDomain);
		System.out.println(loginResponseDTO.toString());

		if (loginResponseDTO == null) {
			// Redis에 Data를 넣어두고 socialSignUp 주소를 보내도록.
			System.out.println("IF");
			String redirect_url = String.format("http://localhost:8080/signup/social/%s", socialDomain);

			return ResponseEntity.badRequest()
					.header("Location", redirect_url)
					.contentType(MediaType.APPLICATION_JSON)
					.body(loginResponseDTO);

		} else {
			System.out.println("ELSE");
			socialLoginService.setCookie(request, response);
			String redirect_url = String.format("http://localhost:8080/signup/social/%s", socialDomain);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.LOCATION, redirect_url);

			return ResponseEntity.ok()
					//          .header("Location", redirect_url)
					.headers(headers)
					.contentType(MediaType.APPLICATION_JSON)
					.body(loginResponseDTO);
		}
	}

	@PostMapping("/social/{socialDomain}")
	public void socialSignUp(@PathVariable String socialDomain, @RequestParam String sessionId, @RequestBody Object obj) {
		signUpService.socialSignUp(null, null, socialDomain);
	}
}
