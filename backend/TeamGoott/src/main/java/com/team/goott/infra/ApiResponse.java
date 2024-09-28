package com.team.goott.infra;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
	private String status;
	private String message;
	private T data;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime requestTime;

    // �⺻ ������
    public ApiResponse() {
        this.requestTime = LocalDateTime.now();
    }

    // ���� ����� ������
    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
        this.requestTime = LocalDateTime.now();
    }

    // ������ ����� ������
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.requestTime = LocalDateTime.now();
    }
	
}
