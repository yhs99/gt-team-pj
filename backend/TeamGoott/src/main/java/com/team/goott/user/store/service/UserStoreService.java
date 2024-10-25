package com.team.goott.user.store.service;

import java.util.List;
import java.util.Map;


public interface UserStoreService {

	Map<String, Object> getAllStores() throws Exception;

	List<Object> getStoreById(int storeId) throws Exception;

	Map<String, Object> getDetailsStoreById(int storeId) throws Exception;

	Map<String, Object> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

}
