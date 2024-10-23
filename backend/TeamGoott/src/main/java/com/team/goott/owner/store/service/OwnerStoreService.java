package com.team.goott.owner.store.service;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreService {

	int createStore(StoreDTO store) throws Exception ;

	StoreDTO login(String id, String pw);

	boolean register(OwnerDTO ownerDTO);

}
