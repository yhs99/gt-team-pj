package com.team.goott.infra;

public class UserNotMatchException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public UserNotMatchException() {
		super();
		this.message = "데이터와 요청한 유저 아이디가 일치하지 않습니다.";
	}
	
	public String toString() {
		return this.message;
	}
}
