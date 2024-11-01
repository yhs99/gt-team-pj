package com.team.goott.infra;

public class StoreNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public StoreNotFoundException() {
		super();
		this.message = "유효하지 않은 매장입니다.";
	}
	public StoreNotFoundException(String message) {
		super();
		this.message = message;
	}
	
	public String toString() {
		return this.message;
	}
}
