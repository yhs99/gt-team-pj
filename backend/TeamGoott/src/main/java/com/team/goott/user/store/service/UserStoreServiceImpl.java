package com.team.goott.user.store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.StoreCategoryVO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.store.persistence.AdminStoreDAO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.user.domain.StoreCategoryDTO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreFiltersVO;
import com.team.goott.user.store.persistence.UserStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreServiceImpl implements UserStoreService {
	
	@Inject
	private UserStoreDAO userStoreDAO;
	
	@Inject
	private AdminStoreDAO adminStoreDAO;
	
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
		filter.setRotations(userStoreDAO.getStoreFiltersRotationCode());
		return filter;
	}

	@Override
	public List<String> selectCouponName(int storeId) {
		// storeId로 couponName조회
		return userStoreDAO.selectCouponNameByStoreId(storeId); 
	}

	@Override
	public List<StoreCategoryDTO> getStoresByCategory(String categoryCodeId) {
		// categoryId로 가게리스트 조회
		return userStoreDAO.selectStoresByCategory(categoryCodeId);
	}

	@Override
	public List<ReserveSlotsDTO> getAllReserveSlots(int storeId, String date) {
		// storeId와 date로 ReserveSlots가져오기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("date", date);
		map.put("storeId", storeId);
		
		return adminStoreDAO.getExistingSlots(map);
	
	}

}
