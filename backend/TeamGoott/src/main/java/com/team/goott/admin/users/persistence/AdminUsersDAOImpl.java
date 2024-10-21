package com.team.goott.admin.users.persistence;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AdminUsersDAOImpl implements AdminUsersDAO {

	private final static String NS = "com.team.mappers.admin.users.adminUsersMapper.";
	
	@Inject
	private SqlSession ses;
	
	@Override
	public List<UserDTO> getAllUsers() {
		return ses.selectList(NS+"getAllUsers");
	}

	@Override
	public UserDTO getUserInfoByUserId(int userId) {
		return ses.selectOne(NS+"getUserInfoByUserId", userId);
	}

	@Override
	public int userInfoUpdate(Map<String, String> user) {
		return ses.update(NS+"userInfoUpdate", user);
	}

}
