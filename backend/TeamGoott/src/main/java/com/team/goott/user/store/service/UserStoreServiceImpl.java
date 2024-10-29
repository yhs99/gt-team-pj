package com.team.goott.user.store.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreFiltersVO;
import com.team.goott.user.store.persistence.UserStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreServiceImpl implements UserStoreService {
	
	@Inject
	private UserStoreDAO userStoreDAO;
	
	@Override
	public List<StoreDTO> getAllStores() throws Exception {
		return userStoreDAO.getAllStores();
	}

	@Override
	public List<Object> getStoreById(int storeId) throws Exception {
		return userStoreDAO.getStoreById(storeId);
	}

	@Override
	public List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception{
		return userStoreDAO.getStoresByCategoriesAndSidos(categoryCodeIds, sidoCodeIds);
	}

	@Override
	public StoreFiltersVO getStoreFilters() {
		StoreFiltersVO filter = new StoreFiltersVO();
		filter.setCategories(userStoreDAO.getStoreFilterCategory());
		filter.setFacilities(userStoreDAO.getStoreFiltersFacilityCode());
		filter.setSidoCodes(userStoreDAO.getStoreFiltersSidoCode());
		return filter;
	}

}
