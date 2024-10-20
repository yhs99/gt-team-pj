package com.team.goott.infra;

public class ImageUploadFailedException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public ImageUploadFailedException() {
		super();
		this.message = "이미지 업로드 중 오류가 발생했습니다.";
	}
	
	public String toString() {
		return this.message;
	}
}
