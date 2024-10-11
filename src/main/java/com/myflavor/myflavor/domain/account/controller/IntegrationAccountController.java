package com.myflavor.myflavor.domain.account.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myflavor.myflavor.common.provider.JWT.JwtProvider;
import com.myflavor.myflavor.domain.account.DTO.request.UserSignUpRequestDTO;
import com.myflavor.myflavor.domain.account.DTO.response.LoginResponseDTO;
import com.myflavor.myflavor.domain.account.DTO.service.UserServiceDTO;
import com.myflavor.myflavor.domain.account.service.IntegrationAccountService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/account", produces = "application/json")
public class IntegrationAccountController {

	private IntegrationAccountService integrationAccountService;
	private JwtProvider jwtProvider;

	public IntegrationAccountController(IntegrationAccountService integrationAccountService, JwtProvider jwtProvider) {
		this.integrationAccountService = integrationAccountService;
		this.jwtProvider = jwtProvider;
	}

	@PostMapping
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody UserSignUpRequestDTO userRequestDTO,
		HttpServletRequest request,
		HttpServletResponse response) {

		HttpHeaders headers = new HttpHeaders();

		UserServiceDTO userServiceDTO = integrationAccountService.signIn(userRequestDTO);
		integrationAccountService.setCookie(userServiceDTO, request, response);

		LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
			.userName(userServiceDTO.getUserName())
			.userEmail(userServiceDTO.getUserEmail())
			.build();

		return ResponseEntity.ok()
			.headers(headers)
			.contentType(MediaType.APPLICATION_JSON)
			.body(loginResponseDTO);
	}

	// @GetMapping("/login")
	// public ResponseEntity<?> getlogin(@RequestBody UserSignUpRequestDTO userRequestDTO, HttpServletRequest request,
	// 	HttpServletResponse response) {
	// 	// login이 된다면 JWT Token을 넘겨줄 수 있도록 해야한다.
	// 	// HttpSession httpSession = request.getSession();
	//
	// 	UserServiceDTO userServiceDTO = integrationAccountService.signUp(userRequestDTO);
	// 	integrationAccountService.setCookie(request, response);
	// 	HttpHeaders headers = new HttpHeaders();
	//
	// 	System.out.println("login");
	// 	return ResponseEntity.ok()
	// 		.headers(headers)
	// 		.contentType(MediaType.APPLICATION_JSON)
	// 		.build();
	// }
}
