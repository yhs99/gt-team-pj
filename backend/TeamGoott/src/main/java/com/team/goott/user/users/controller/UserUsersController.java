package com.team.goott.user.users.controller;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.users.service.AdminUsersService;
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

	private final RestTemplate restTemplate = new RestTemplate();

	private final String UNAUTHORIZED_MESSAGE = "로그인 정보가 없습니다.";
	// 로그인 상태 체크
	@GetMapping("/status")
	public ResponseEntity<Object> checkStatus(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
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
			map.put("loginType", "user");
			
		}else if(sessionData instanceof StoreDTO) {
			map.put("name", ((StoreDTO) sessionData).getStoreName());
			if (map.get("name") == null) {
				Map<String, String> noStoreInfoResult = new HashMap<String, String>();
				noStoreInfoResult.put("message", "등록된 가게가 없습니다. 가게를 등록해주세요");
				noStoreInfoResult.put("redirect", "/view/owner/registerStore");
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(noStoreInfoResult);
			}
			map.put("loginType", "store");
		}else if(sessionData instanceof AdminDTO) {
			map.put("name", ((AdminDTO) sessionData).getId());
			map.put("loginType", "admin");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
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

	    // 로그인 시도
	    StoreDTO storeInfo = ownerService.login(id, password);
	    
	    if (storeInfo == null) {
	        // 로그인 실패: 잘못된 아이디 또는 비밀번호
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 아이디 또는 비밀번호입니다.");
	    }

	    // 로그인 성공 후, ownerId를 참조하는 store 정보 가져오기
	    StoreDTO loginStore = ownerService.getStoreByOwnerId(storeInfo.getOwnerId());
	    log.info("로그인시 store정보 : " + loginStore);

	    if (loginStore == null || loginStore.getStoreId() == 0) {
	        // 가게가 등록되지 않았지만 로그인은 성공
	        loginResponse.put("loginSuccess", "" + storeInfo.getOwnerId());
	        session.setAttribute("store", storeInfo);
	        log.info("가게가 등록되지 않음. 로그인 성공 세션 생성 : {}", session.getAttribute("store"));
	        log.info("로그인 성공 세션 아이디 : {}", session.getId());
	        return ResponseEntity.status(HttpStatus.ACCEPTED).body("가게 정보 등록이 필요합니다.");
	    } else {
	        // 로그인 및 가게 등록 성공
	        loginResponse.put("loginSuccess", "" + loginStore.getStoreId());
	        session.setAttribute("store", loginStore);
	        log.info("로그인 성공 세션 생성 : {}", session.getAttribute("store"));
	        log.info("로그인 성공 세션 아이디 : {}", session.getId());
	    }

	    // 세션 쿠키 생성 및 설정
	    Cookie cookie = new Cookie("JSESSIONID", session.getId());
	    cookie.setMaxAge(1800); // 30분 = 1800초
	    cookie.setPath("/");
	    response.addCookie(cookie);

	    // 로그인 성공 응답
	    return ResponseEntity.ok(loginResponse);
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
	
	@GetMapping("/socialLogin")
	public ResponseEntity<Object> socialLogin(HttpSession session
											, @RequestParam String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "6219ccda509680348fe4b5d460baf4b8");
        body.add("redirect_uri", "https://smartreserve.store/api/socialLogin");
        body.add("code", code);

		String url = "https://kauth.kakao.com/oauth/token";
		String getUserInfo = "https://kapi.kakao.com/v2/user/me";
		List<String> getUserInfoLists = Arrays.asList("kakao_account.profile", "kakao_account.name", "kakao_account.email", "kakao_account.gender");
		String listAsString = "[" + String.join(",", getUserInfoLists) + "]";

		String uri = UriComponentsBuilder.fromHttpUrl(getUserInfo)
                .queryParam("listParam", listAsString)
                .build()
                .toString();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        Map<String, Object> responseBody = response.getBody();
        log.info((String) responseBody.get("access_token"));
        

		HttpHeaders userInfoHeaders = new HttpHeaders();
		userInfoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String access_token = (String) responseBody.get("access_token");
		userInfoHeaders.add("Authorization", "Bearer " + access_token);
		
        HttpEntity<MultiValueMap<String, String>> requestUserInfo = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfo = restTemplate.exchange(uri, HttpMethod.GET, requestUserInfo, Map.class);
        Map<String, Map<String, Object>> responseUserInfo = userInfo.getBody();
        UserDTO userByEmail = usersService.loginByKakao((String) responseUserInfo.get("kakao_account").get("email"));
    	HttpHeaders redirectHeader = new HttpHeaders();
        if(userByEmail != null) {
        	session.setAttribute("user", userByEmail);
        	redirectHeader.setLocation(URI.create("/"));
        	return ResponseEntity.status(HttpStatus.FOUND).headers(redirectHeader).build();
        }else {
        	redirectHeader.setLocation(URI.create("/view/user/register?email="+(String) responseUserInfo.get("kakao_account").get("email")));
        	return ResponseEntity.status(HttpStatus.FOUND).headers(redirectHeader).build();
        }
	}
}