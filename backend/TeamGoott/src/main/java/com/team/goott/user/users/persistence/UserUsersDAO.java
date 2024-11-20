package com.team.goott.user.users.persistence;

import com.team.goott.user.domain.UserDTO;

public interface UserUsersDAO {

	UserDTO login(String id, String password);

	UserDTO getUserInfoByKakao(String email);
}
