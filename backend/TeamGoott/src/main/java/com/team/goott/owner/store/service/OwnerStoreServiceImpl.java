package com.team.goott.owner.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerStoreServiceImpl implements OwnerStoreService {
	
	@Autowired
	private OwnerStoreDAO ownerStoreDao;
	
	
	@Override
	public int createStore(StoreDTO store) throws Exception {
		return ownerStoreDao.createStore(store);
	}

}
