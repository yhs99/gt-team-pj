package com.team.goott.user.users.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserUsersDAOImpl implements UserUsersDAO {
	private final static String NS = "com.team.mappers.user.users.userUsersMapper.";

	@Inject
	private SqlSession ses;

	@Override
	public UserDTO login(String id, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", id);
		map.put("password", password);
		return ses.selectOne(NS + "userLoginInfo", map);
	}

}
