package com.team.goott.infra;

public class UserNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public UserNotFoundException() {
		super();
		this.message = "해당 유저를 찾을 수 없습니다.";
	}
	
	public String toString() {
		return this.message;
	}
}
