package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreDAO {
	int createStore(StoreDTO store) throws Exception;
	StoreDTO login(String id, String pw);
	boolean ownerRegister(OwnerDTO ownerDTO);

}
