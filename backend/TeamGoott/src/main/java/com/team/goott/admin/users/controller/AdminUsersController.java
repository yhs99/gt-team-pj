package com.team.goott.admin.users.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.users.service.AdminUsersService;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminUsersController {
	
	@Autowired
	private AdminUsersService ads;
	
	// 전체 사용자 목록
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers() {
		return ResponseEntity.ok(ads.getAllUsers());
	}
	
	// 특정 사용자 정보
	@GetMapping("/users/{userId}")
	public ResponseEntity<Object> getUserByUserId(HttpSession session, @PathVariable int userId) {
		UserDTO userInfo = ads.getUserInfo(userId);
		if(userInfo == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저입니다.");
		}
		return ResponseEntity.ok(userInfo);
	}
}
