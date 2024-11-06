package com.team.goott.admin.users.service;

import com.team.goott.admin.domain.AdminDTO;

import java.util.Map;

import com.team.goott.infra.UserNotFoundException;
import com.team.goott.infra.UserNotMatchException;
import com.team.goott.admin.domain.UserDTO;
import com.team.goott.admin.domain.UserSearchFilter;
import com.team.goott.user.register.domain.UserRegisterDTO;

public interface AdminUsersService {
	Map<String, Object> getAllUsers(UserSearchFilter filter);
	UserDTO getUserInfo(int userId);
	int patchUserInfo(UserRegisterDTO userUpdateData, int userId) throws UserNotFoundException, UserNotMatchException;
	AdminDTO login(String id, String password);
}
