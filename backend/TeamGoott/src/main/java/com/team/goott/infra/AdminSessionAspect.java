package com.team.goott.infra;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.AdminOnly;

@Aspect
@Component
public class AdminSessionAspect {
	
	@Autowired
	private HttpSession session;
	private final String UNAUTHORIZED_MESSAGE = "권한이 없습니다.";
	
	@Order(1)
	@Around("@annotation(adminOnly)")
	public Object checkAdminSession(ProceedingJoinPoint joinPoint, AdminOnly adminOnly) throws Throwable {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		
		if(adminSession == null) {
			ApiResponse<Object> apiResponse = new ApiResponse<>("fail", UNAUTHORIZED_MESSAGE);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
		}
		return joinPoint.proceed();
	}
}
