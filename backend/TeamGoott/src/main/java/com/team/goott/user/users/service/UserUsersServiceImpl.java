package com.team.goott.user.users.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.users.persistence.UserUsersDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserUsersServiceImpl implements UserUsersService {
	@Inject
	private UserUsersDAO usersDao;

	@Override
	public UserDTO login(String id, String password) {
		// TODO Auto-generated method stub
		UserDTO userDTO = usersDao.login(id, password);
		return userDTO;
	}

	@Override
	public UserDTO loginByKakao(String email) {
		return usersDao.getUserInfoByKakao(email);
	}

}
