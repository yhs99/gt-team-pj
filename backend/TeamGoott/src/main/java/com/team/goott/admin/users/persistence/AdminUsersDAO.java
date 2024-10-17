package com.team.goott.admin.users.persistence;

import java.util.List;

import com.team.goott.user.domain.UserDTO;

public interface AdminUsersDAO {
	List<UserDTO> getAllUsers();
	UserDTO getUserInfoByUserId(int userId);
}
