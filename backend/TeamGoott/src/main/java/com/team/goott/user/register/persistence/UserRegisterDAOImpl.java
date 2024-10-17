package com.team.goott.user.register.persistence;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserRegisterDAOImpl implements UserRegisterDAO {
	
	private final static String NS = "com.team.mappers.user.register.userRegisterMapper.";
	
	@Inject
	private SqlSession ses;

	@Override
	public int userRegisterProcess(UserDTO user) {
		return ses.insert(NS + "userRegisterProcess", user);
	}

}
