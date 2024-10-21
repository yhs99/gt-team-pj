package com.team.goott.user.store.service;

import java.util.List;

import com.team.goott.user.domain.StoreDTO;

public interface UserStoreService {

	List<StoreDTO> getAllStores() throws Exception;

	List<Object> getStoreById(int storeId) throws Exception;

	List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

}
