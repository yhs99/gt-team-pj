package com.team.goott.infra;

public class ImageDeleteFailedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public ImageDeleteFailedException() {
		super();
		this.message = "이미지 객체 삭제 중 오류가 발생했습니다.";
	}
	
	public ImageDeleteFailedException(String message) {
		super();
		this.message = message;
	}
	
	public ImageDeleteFailedException(String message, String fileName) {
		super();
		this.message = message + " :: " + fileName;
	}
	
	public String toString() {
		return this.message;
	}
}
