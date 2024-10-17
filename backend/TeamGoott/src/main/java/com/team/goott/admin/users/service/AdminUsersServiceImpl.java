package com.team.goott.admin.users.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.users.persistence.AdminUsersDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminUsersServiceImpl implements AdminUsersService {
	
	@Inject
	private AdminUsersDAO adminDAO;
	
	@Override
	public AdminDTO login(String id, String password) {
		
		AdminDTO adminDTO = adminDAO.login(id, password);
		return adminDTO;
	}

}
