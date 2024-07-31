package com.myflavor.myflavor.domain.feed.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/heart", produces = "application/json")
public class HeartController {

	@GetMapping("/up/feed")
	public void upFeed() {
	}

	@GetMapping("/down/feed")
	public void downFeed() {
	}

	@GetMapping("/up/comment")
	public void upComment() {
	}

	@GetMapping("/down/comment")
	public void downComment() {
	}
}
