package com.team.goott.user.store.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.store.persistence.UserStoreDAO;
import com.team.goott.user.store.persistence.UserStoreDAOImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreServiceImpl implements UserStoreService {
	private UserStoreDAO userStoreDAO = new UserStoreDAOImpl();
	
	@Override
	public List<StoreDTO> getAllStores() throws Exception {
		return userStoreDAO.getAll();
	}

}
