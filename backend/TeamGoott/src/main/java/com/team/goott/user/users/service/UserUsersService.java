package com.team.goott.user.users.service;

import com.team.goott.user.domain.UserDTO;

public interface UserUsersService {

	UserDTO login(String id, String password);

	UserDTO loginByKakao(String email);

}
