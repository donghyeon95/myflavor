package com.myflavor.myflavor.domain.profile.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myflavor.myflavor.domain.profile.dto.ProfileResponseDTO;
import com.myflavor.myflavor.domain.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "/profile", produces = "application/json")
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping("/{userName}")
	public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable String userName, @RequestParam int p,
		@RequestParam int s) {
		Pageable pageable = PageRequest.of(p, s);

		return profileService.getProfiles(userName, pageable);
	}

}
