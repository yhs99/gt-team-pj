package com.team.goott.admin.users.service;

import com.team.goott.admin.domain.AdminDTO;

public interface AdminUsersService {

	AdminDTO login(String id, String password);

}
