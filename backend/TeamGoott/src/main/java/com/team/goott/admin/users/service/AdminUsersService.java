package com.team.goott.admin.users.service;

import java.util.Map;

import com.team.goott.user.domain.UserDTO;

public interface AdminUsersService {
	Map<String, Object> getAllUsers();
	UserDTO getUserInfo(int userId);
}
