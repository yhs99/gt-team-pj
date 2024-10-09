package com.team.goott.infra;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ApiLoggingAspect {
	
	@Autowired
	private HttpServletRequest hsr;
	
	@Description("Controller에 대한 로깅 AOP")
	@Around("execution(* com.team.goott..controller..*(..))")
	public Object apiLogging(ProceedingJoinPoint joinPoint) throws Throwable{
		 // 요청 시작 시간 기록
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = hsr;
		Object result;
		try {
            // 실제 메서드 호출
            result = joinPoint.proceed();
        } finally {
            // 요청 종료 시간 및 응답 시간 계산
            long elapsedTime = System.currentTimeMillis() - startTime;

            // 로그 메시지 생성
            log.info("API CALLED :: [" + LocalDateTime.now() +"] "+ request.getMethod() + " - " + request.getRequestURI() + ", 처리시간 :: " + elapsedTime + "ms");
        }
		
		return result;
	}
}
