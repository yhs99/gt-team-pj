package com.team.goott.owner.menu.controller;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.menu.service.OwnerMenuService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OwnerMenuController {
	
	@Inject
	OwnerMenuService service;
	
	@GetMapping("/menu")
	public ResponseEntity<Object> getMenu(){
		log.info("menuAPI 호출");
		return null;
	}

}
