package com.team.goott.user.domain;

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
public class UserDTO {
	private int userId;
	private String email;
	private String password;
	private String name;
	private String mobile;
	private String gender;
	private String profileImageUrl;
	private String profileImageName;
}
