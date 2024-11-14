package com.team.goott.infra;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.AdminOnly;
import com.team.goott.owner.domain.OwnerOnly;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.UserOnly;

@Aspect
@Component
public class AdminSessionAspect {
	
	@Autowired
	private HttpSession session;
	private final String FORBIDDEN_MESSAGE = "권한이 없습니다.";
	private final String UNAUTHORIZED_MESSAGE = "로그인이 필요한 서비스입니다.";

	// 어드민 세션 AOP
	@Around("@annotation(adminOnly)")
	public Object checkAdminSession(ProceedingJoinPoint joinPoint, AdminOnly adminOnly) throws Throwable {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		
		if(userSession != null || storeSession != null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", FORBIDDEN_MESSAGE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}else if(adminSession == null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", UNAUTHORIZED_MESSAGE);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}
		return joinPoint.proceed();
	}
	
	// 유저 세션 AOP
	@Around("@annotation(userOnly)")
	public Object checkUserSession(ProceedingJoinPoint joinPoint, UserOnly userOnly) throws Throwable {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");

		if(storeSession != null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", FORBIDDEN_MESSAGE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}else if(userSession == null && adminSession == null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", UNAUTHORIZED_MESSAGE);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}
		return joinPoint.proceed();
	}
	
	// 점주 세션 AOP
	@Around("@annotation(ownerOnly)")
	public Object checkOwnerSession(ProceedingJoinPoint joinPoint, OwnerOnly ownerOnly) throws Throwable {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		
		if(userSession != null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", FORBIDDEN_MESSAGE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}else if(storeSession == null && adminSession == null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", UNAUTHORIZED_MESSAGE);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}
		
		return joinPoint.proceed();
	}
}
