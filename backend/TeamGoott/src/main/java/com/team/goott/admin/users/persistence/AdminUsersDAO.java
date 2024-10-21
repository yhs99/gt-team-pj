package com.team.goott.admin.users.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.user.domain.UserDTO;

public interface AdminUsersDAO {
	List<UserDTO> getAllUsers();
	UserDTO getUserInfoByUserId(int userId);
	int userInfoUpdate(Map<String, String> user);
}
