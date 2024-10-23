package com.team.goott.owner.store.service;



import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreService {

	StoreDTO login(String id, String pw);

	boolean register(OwnerDTO ownerDTO);

}
