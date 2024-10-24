package com.team.goott.infra;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ValidationException(String message) {
		super();
		this.message = message;
	}
	
	public String toString() {
		return this.message;
	}
}
