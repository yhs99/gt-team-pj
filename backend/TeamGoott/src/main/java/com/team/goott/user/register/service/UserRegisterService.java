package com.team.goott.user.register.service;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.infra.ValidationException;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.register.domain.UserRegisterDTO;

public interface UserRegisterService {
	public int userRegister(UserRegisterDTO user) throws ValidationException, Exception;

	public UserDTO userInfo(int userId);

	public void userUpdate(UserDTO userDTO, MultipartFile imageFile, HttpSession session) throws ValidationException, Exception;
}
