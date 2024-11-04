package com.team.goott.admin.users.persistence;

import com.team.goott.admin.domain.AdminDTO;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.UserDTO;
import com.team.goott.admin.domain.UserSearchFilter;
public interface AdminUsersDAO {
	List<UserDTO> getAllUsers(UserSearchFilter filter);
	UserDTO getUserInfoByUserId(int userId);
	int userInfoUpdate(Map<String, String> user);
	AdminDTO login(String id, String password);
}
