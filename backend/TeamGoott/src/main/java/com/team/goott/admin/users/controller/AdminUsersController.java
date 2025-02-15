package com.team.goott.admin.users.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.admin.domain.AdminOnly;
import com.team.goott.admin.users.service.AdminUsersService;
import com.team.goott.infra.UserNotFoundException;
import com.team.goott.admin.domain.UserDTO;
import com.team.goott.admin.domain.UserSearchFilter;
import com.team.goott.user.register.domain.UserRegisterDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminUsersController {
	
	@Autowired
	private AdminUsersService ads;
	
	//전체 사용자 목록
	@AdminOnly
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(HttpSession session
										, @ModelAttribute UserSearchFilter filter) {
		return ResponseEntity.ok(ads.getAllUsers(filter));
	}
	
	//사용자 정보
	@AdminOnly
	@GetMapping("/users/{userId}")
	public ResponseEntity<Object> getUserByUserId(HttpSession session, @PathVariable int userId) {
		UserDTO userInfo = ads.getUserInfo(userId);
		if(userInfo == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저입니다.");
		}
		return ResponseEntity.ok(userInfo);
	}
	
	//유저 정보 수정
	@AdminOnly
	@PatchMapping("/users/{userId}")
	public ResponseEntity<Object> putUserInfo(HttpSession session
											, @PathVariable int userId
											, @ModelAttribute UserRegisterDTO userUpdateData
									        , @RequestParam(name="uploadImage", required = false) MultipartFile imageFile) {
		try {
			userUpdateData.setImageFile(imageFile);
			ads.patchUserInfo(userUpdateData, userId);
		}catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		return ResponseEntity.ok("수정 완료");
	}
	
}
