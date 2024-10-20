package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreDAO {

	int createStore(StoreDTO store) throws Exception;

}
