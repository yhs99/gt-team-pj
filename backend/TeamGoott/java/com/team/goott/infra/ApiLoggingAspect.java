package com.team.goott.infra;

import java.time.LocalDateTime;
import java.util.Arrays;

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
		// 메서드의 파라미터 값 가져오기
        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args);
		try {
            // 실제 메서드 호출
            result = joinPoint.proceed();
        } finally {
            // 요청 종료 시간 및 응답 시간 계산
            long elapsedTime = System.currentTimeMillis() - startTime;

            // 로그 메시지 생성
            String logMessage = String.format(
                    "API CALLED :: [%s] %s - %s, Params: %s, 처리시간 :: %dms",
                    LocalDateTime.now(),
                    request.getMethod(),
                    request.getRequestURI(),
                    params,
                    elapsedTime
                );

            // 로그 출력
            log.info(logMessage);
        }
		
		return result;
	}
}
