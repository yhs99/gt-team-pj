package com.team.goott.user.register.persistence;

import com.team.goott.user.domain.UserDTO;

public interface UserRegisterDAO {
	public int userRegisterProcess(UserDTO user);
}
