package com.team.goott.user.store.service;

import java.util.List;

import com.team.goott.admin.domain.StoreCategoryVO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.user.domain.StoreCategoryDTO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreFiltersVO;

public interface UserStoreService {

	List<StoreDTO> getAllStores() throws Exception;

	List<Object> getStoreById(int storeId) throws Exception;

	List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

	StoreFiltersVO getStoreFilters();

	List<String> selectCouponName(int storeId);

	List<StoreCategoryDTO> getStoresByCategory(String categoryCodeId);

	List<ReserveSlotsDTO>  getAllReserveSlots(int storeId, String date);
}
