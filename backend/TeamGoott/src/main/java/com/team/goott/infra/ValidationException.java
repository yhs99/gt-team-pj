package com.team.goott.infra;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public ValidationException(String msg) {
		super();
		this.msg = msg;
	}
	
	public String toString() {
		return this.msg;
	}
}
