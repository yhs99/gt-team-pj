package com.team.goott.infra;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ApiLoggingAspect {
	
	@Autowired
	private HttpServletRequest hsr;
	
	@Around("execution(* com.team.goott..*(..))")
	public Object apiLogging(ProceedingJoinPoint joinPoint) throws Throwable{
		 // ��û ���� �ð� ���
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = hsr;
		Object result;
		try {
            // ���� �޼��� ȣ��
            result = joinPoint.proceed();
        } finally {
            // ��û ���� �ð� �� ���� �ð� ���
            long elapsedTime = System.currentTimeMillis() - startTime;
            //String params = Arrays.toString(joinPoint.getArgs());

            // �α� �޽��� ����
            log.info("API CALLED :: [" + LocalDateTime.now() +"] "+ request.getMethod() + " - " + request.getRequestURI() + ", ó���ð� :: " + elapsedTime);
        }
		
		return result;
	}
}
