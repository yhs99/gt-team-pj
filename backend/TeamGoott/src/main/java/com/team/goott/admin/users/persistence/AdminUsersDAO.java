package com.team.goott.admin.users.persistence;

import com.team.goott.admin.domain.AdminDTO;

public interface AdminUsersDAO {

	AdminDTO login(String id, String password);

}
