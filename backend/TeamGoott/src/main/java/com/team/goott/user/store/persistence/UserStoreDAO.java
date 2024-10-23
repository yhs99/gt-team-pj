package com.team.goott.user.store.persistence;

import java.util.List;

import com.team.goott.user.domain.StoreDTO;

public interface UserStoreDAO {

	List<StoreDTO> getAllStores() throws Exception;

	List<Object> getStoreById(int storeId) throws Exception;

	List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

}
