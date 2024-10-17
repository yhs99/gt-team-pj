package com.team.goott.admin.users.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.admin.users.persistence.AdminUsersDAO;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminUsersServiceImpl implements AdminUsersService {
	
	@Autowired
	private AdminUsersDAO dao;
	
	@Override
	public Map<String, Object> getAllUsers() {
		List<UserDTO> userLists = dao.getAllUsers();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("userCount", userLists.size());
		returnMap.put("userLists", userLists);
		return returnMap;
	}

	@Override
	public UserDTO getUserInfo(int userId) {
		return dao.getUserInfoByUserId(userId);
	}

}
