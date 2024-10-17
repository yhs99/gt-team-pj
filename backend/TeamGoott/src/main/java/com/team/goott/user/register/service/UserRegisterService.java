package com.team.goott.user.register.service;

import com.team.goott.infra.ValidationException;
import com.team.goott.user.register.domain.UserRegisterDTO;

public interface UserRegisterService {
	public int userRegister(UserRegisterDTO user) throws ValidationException, Exception;
}
