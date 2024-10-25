package com.team.goott.user.users.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.users.service.AdminUsersService;
import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.service.OwnerStoreService;
import com.team.goott.user.domain.LoginDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.users.service.UserUsersService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserUsersController {

	@Inject
	private UserUsersService usersService;

	@Inject
	private OwnerStoreService ownerService;

	@Inject
	private AdminUsersService adminService;

	// 로그인 상태 체크
	@GetMapping("/status")
	public ResponseEntity<Object> checkStatus(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 되어있지 않습니다.");
		}

		Object sessionData = null;
		Map<String, String> map = new HashMap<String, String>();
		
		if(session.getAttribute("user") != null) {
			sessionData = (UserDTO) session.getAttribute("user");
		}else if(session.getAttribute("store") != null) {
			sessionData = (StoreDTO) session.getAttribute("store");
		}else if(session.getAttribute("admin") != null) {
			sessionData = (AdminDTO) session.getAttribute("admin");
		}
		
		if(sessionData instanceof UserDTO) {
			map.put("name", ((UserDTO) sessionData).getName());
			map.put("profileImageUrl", ((UserDTO) sessionData).getProfileImageUrl());
		}else if(sessionData instanceof StoreDTO) {
			map.put("name", ((StoreDTO) sessionData).getStoreName());
		}else if(sessionData instanceof AdminDTO) {
			map.put("name", ((AdminDTO) sessionData).getId());
		}
		
		return ResponseEntity.ok(map);
		
	}
	// 통합 로그인
	@PostMapping("/login")
	public ResponseEntity<Object> userLoginRequest(LoginDTO loginDTO, HttpSession session,
			HttpServletResponse response) {
		Map<String, String> loginResponse = new HashMap<>();

		if ("0".equals(loginDTO.getLoginGroup())) {
			// 회원
			return handleUserLogin(loginDTO.getId(), loginDTO.getPassword(), session, response, loginResponse);
		} else if ("1".equals(loginDTO.getLoginGroup())) {
			// 점주
			return handleOwnerLogin(loginDTO.getId(), loginDTO.getPassword(), session, response, loginResponse);
		} else if ("99".equals(loginDTO.getLoginGroup())) {
			// 관리자
			return handleAdminLogin(loginDTO.getId(), loginDTO.getPassword(), session, response, loginResponse);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 로그인 그룹입니다.");
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Object> userLogout(HttpSession session) {
		session.invalidate();
		
		return ResponseEntity.ok("로그아웃 완료");
	}

	// 회원 로그인 메서드
	private ResponseEntity<Object> handleUserLogin(String id, String password, HttpSession session,
			HttpServletResponse response, Map<String, String> loginResponse) {
		UserDTO loginInfo = usersService.login(id, password);

		if (loginInfo != null) {
			loginResponse.put("loginSuccess", loginInfo.getName());
			session.setAttribute("user", loginInfo);
			log.info("로그인 성공 세션 생성 : {}", session.getAttribute("user"));
			log.info("로그인 성공 세션 아이디 : {}", session.getId());

			Cookie cookie = new Cookie("JSESSIONID", session.getId());
			cookie.setMaxAge(1800); // 30분 = 1800초
			cookie.setPath("/");
			response.addCookie(cookie);

			return ResponseEntity.ok(loginResponse);
		} else {
			System.out.println("로그인 실패");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 아이디 또는 비밀번호입니다.");
		}
	}

	// 점주 로그인 메서드
	private ResponseEntity<Object> handleOwnerLogin(String id, String password, HttpSession session,
			HttpServletResponse response, Map<String, String> loginResponse) {
		StoreDTO storeInfo = ownerService.login(id, password);

		if (storeInfo != null) {

			loginResponse.put("loginSuccess", "" + storeInfo.getStoreId());

			session.setAttribute("store", storeInfo);

			log.info("로그인 성공 세션 생성 : {}", session.getAttribute("store"));
			log.info("로그인 성공 세션 아이디 : {}", session.getId());

			Cookie cookie = new Cookie("JSESSIONID", session.getId());
			cookie.setMaxAge(1800); // 30분 = 1800초
			cookie.setPath("/");
			response.addCookie(cookie);

			return ResponseEntity.ok(loginResponse);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 아이디 또는 비밀번호입니다.");
		}
	}

	// 관리자 로그인 메서드
	private ResponseEntity<Object> handleAdminLogin(String id, String password, HttpSession session,
			HttpServletResponse response, Map<String, String> loginResponse) {
		AdminDTO adminDTO = adminService.login(id, password);

		if (adminDTO != null) {

			loginResponse.put("loginSuccess", "" + adminDTO.getId());

			session.setAttribute("admin", adminDTO);

			log.info("로그인 성공 세션 생성 : {}", session.getAttribute("admin"));
			log.info("로그인 성공 세션 아이디 : {}", session.getId());

			Cookie cookie = new Cookie("JSESSIONID", session.getId());
			cookie.setMaxAge(1800); // 30분 = 1800초
			cookie.setPath("/");
			response.addCookie(cookie);

			return ResponseEntity.ok(loginResponse);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 아이디 또는 비밀번호입니다.");
		}
	}
}
