package com.team.goott.admin.users.service;

import java.util.Map;

import com.team.goott.infra.UserNotFoundException;
import com.team.goott.infra.UserNotMatchException;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.register.domain.UserRegisterDTO;

public interface AdminUsersService {
	Map<String, Object> getAllUsers();
	UserDTO getUserInfo(int userId);
	int patchUserInfo(UserRegisterDTO userUpdateData, int userId) throws UserNotFoundException, UserNotMatchException;
}
