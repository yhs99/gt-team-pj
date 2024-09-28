package com.team.goott.infra;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseAspect {
	@Around("execution(* com.team.goott.*.controller..*(..))")
    public Object wrapResponse(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println("aspect �۵�");
		Object result = joinPoint.proceed();

        // ResponseEntity�� ��� ó��
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            int statusCode = responseEntity.getStatusCodeValue();

            if (statusCode >= 200 && statusCode < 300) {
                // ���� ����� ����
                ApiResponse<Object> apiResponse = new ApiResponse<>("success", body);
                return ResponseEntity.status(responseEntity.getStatusCode())
                                     .headers(responseEntity.getHeaders())
                                     .body(apiResponse);
            } else {
                // ������ ����� ����
                String errorMessage = (body != null) ? body.toString() : "An error occurred";
                ApiResponse<Object> apiResponse = new ApiResponse<>("fail", errorMessage);
                return ResponseEntity.status(responseEntity.getStatusCode())
                                     .headers(responseEntity.getHeaders())
                                     .body(apiResponse);
            }
        }

        // ResponseEntity�� �ƴ� ��� ���� �������� ����
        ApiResponse<Object> apiResponse = new ApiResponse<>("success", result);
        return apiResponse;
    }
}
