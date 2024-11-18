package com.team.goott.user.register.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;


import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.infra.ValidationException;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.UserOnly;
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
	public ResponseEntity<Object> userRegister(HttpSession session,@ModelAttribute UserRegisterDTO user) {
		log.info(user.toString());
		int status = 0;
		try {
			status = service.userRegister(user);
		}catch(ValidationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch(DuplicateKeyException | MyBatisSystemException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 이메일입니다.");
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok(status > 0 ? "회원가입이 완료되었습니다. 로그인 해주세요" : "");
	}
	
	// 회원 정보 수정
	@UserOnly
	@PatchMapping
	public ResponseEntity<Object> updateUserInfo(HttpSession session, UserDTO userDTO,MultipartFile imageFile){
		try {
			UserDTO userSession = (UserDTO) session.getAttribute("user");
			userDTO.setUserId(userSession.getUserId());
			service.userUpdate(userDTO,imageFile, session);
		}catch (ValidationException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok("수정 완료 다시 로그인 해주세요.");
	}
	
	//회원 정보 가져오기
	@UserOnly
	@GetMapping
	public ResponseEntity<Object> myUserInfo(HttpSession session){
		UserDTO loginUser=(UserDTO) session.getAttribute("user");
		UserDTO myUserInfo = service.userInfo(loginUser.getUserId());
		myUserInfo.setPassword("");
		return ResponseEntity.ok(myUserInfo);
	}
}
