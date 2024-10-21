package com.team.goott.infra;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<String>> missingParameterHandler(MissingServletRequestParameterException ex) {
		ex.printStackTrace();
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus("error");
        response.setData(ex.getParameterName() + "은 필수항목입니다.");
        response.setRequestTime(LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllExceptions(Exception ex) {
		ex.printStackTrace();
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus("error");
        response.setData(ex.getMessage());
        response.setRequestTime(LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
