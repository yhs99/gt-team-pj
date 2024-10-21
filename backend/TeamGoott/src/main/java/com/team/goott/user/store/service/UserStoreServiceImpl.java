package com.team.goott.user.store.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.store.persistence.UserStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreServiceImpl implements UserStoreService {
	
	@Inject
	private UserStoreDAO userStoreDAO;
	
	@Override
	public List<StoreDTO> getAllStores() throws Exception {
		return userStoreDAO.getAllStores();
	}

}
