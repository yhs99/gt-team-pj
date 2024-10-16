package com.team.goott.user.register.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.infra.ValidationException;
import com.team.goott.user.register.domain.UserRegisterDTO;
import com.team.goott.user.register.service.UserRegisterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserRegisterController {

	@Inject
	private UserRegisterService service;
	
	@PostMapping("/user/register")
	public ResponseEntity<Object> flexibleRegister(HttpSession session, UserRegisterDTO user) {
		int status = 0;
		try {
			service.userRegister(user);
		}catch(ValidationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok(status > 0 ? "회원가입 성공" : "");
	}
}
