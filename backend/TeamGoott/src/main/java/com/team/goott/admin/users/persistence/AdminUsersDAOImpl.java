package com.team.goott.admin.users.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.admin.domain.AdminDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AdminUsersDAOImpl implements AdminUsersDAO {
	private final static String NS = "com.team.mappers.admin.users.adminUsersMapper.";
	
	@Inject
	private SqlSession ses;
	
	@Override
	public AdminDTO login(String id, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("password", password);
		return ses.selectOne(NS+"adminLoginInfo", map);
	}

}
