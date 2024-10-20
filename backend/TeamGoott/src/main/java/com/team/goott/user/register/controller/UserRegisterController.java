package com.team.goott.user.register.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.infra.ValidationException;
import com.team.goott.user.register.domain.UserRegisterDTO;
import com.team.goott.user.register.service.UserRegisterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserRegisterController {

	@Inject
	private UserRegisterService service;
	
	@PostMapping("/register")
	public ResponseEntity<Object> userRegister(HttpSession session, UserRegisterDTO user) {
		log.info(user.toString());
		int status = 0;
		try {
			status = service.userRegister(user);
		}catch(ValidationException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch(DuplicateKeyException | MyBatisSystemException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
		}catch(Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok(status > 0 ? "회원가입 성공" : "");
	}
}
