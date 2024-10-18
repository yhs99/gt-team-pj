package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreDAO {

	StoreDTO login(String id, String pw);

	boolean ownerRegister(OwnerDTO ownerDTO);

}
