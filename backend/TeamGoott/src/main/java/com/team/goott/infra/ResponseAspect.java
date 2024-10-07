package com.team.goott.infra;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResponseAspect {
	
	@Description("이 AOP는 REST API의 일관된 형식을 제공하기 위해 제작한 AOP입니다.")
	@Around("execution(* com.team.goott.*.controller(..))")
    public Object wrapResponse(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = joinPoint.proceed();

        // ResponseEntity인 경우 처리
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            int statusCode = responseEntity.getStatusCodeValue();

            if (statusCode >= 200 && statusCode < 300) {
                // 정상 응답용 매핑
                ApiResponse<Object> apiResponse = new ApiResponse<>("success", body);
                return ResponseEntity.status(responseEntity.getStatusCode())
                                     .headers(responseEntity.getHeaders())
                                     .body(apiResponse);
            } else {
                // 비정상 응답용 매핑
                String errorMessage = (body != null) ? body.toString() : "An error occurred";
                ApiResponse<Object> apiResponse = new ApiResponse<>("fail", errorMessage);
                return ResponseEntity.status(responseEntity.getStatusCode())
                                     .headers(responseEntity.getHeaders())
                                     .body(apiResponse);
            }
        }

        // ResponseEntity가 아닌 경우 정상 응답으로 래핑
        ApiResponse<Object> apiResponse = new ApiResponse<>("success", result);
        return apiResponse;
    }
}
