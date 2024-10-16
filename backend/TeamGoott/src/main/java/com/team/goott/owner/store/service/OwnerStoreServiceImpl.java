package com.team.goott.owner.store.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerStoreServiceImpl implements OwnerStoreService {
	@Inject
	OwnerStoreDAO storeDAO;

	
	@Override
	public StoreDTO login(String id, String pw) {
		// TODO Auto-generated method stub
		StoreDTO storeDTO = storeDAO.login(id,pw);
		return storeDTO;
	}

	
}
