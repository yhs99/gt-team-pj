package com.team.goott.user.register.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserRegisterDTO {
	private String email;
	private String password;
	private String name;
	private String mobile;
	private String gender;
	private MultipartFile imageFile;
}
