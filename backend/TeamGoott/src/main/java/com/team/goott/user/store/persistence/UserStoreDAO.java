package com.team.goott.user.store.persistence;

import java.util.List;

import com.team.goott.user.domain.CouponDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.ScheduleDTO;
import com.team.goott.user.domain.StoreCategoryDTO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreImagesDTO;

public interface UserStoreDAO {

	List<Object> getStoreById(int storeId) throws Exception;

	Object getStore() throws Exception;

	Object getFacility() throws Exception;

	Object getSchedule() throws Exception;

	Object getSidocode() throws Exception;

	Object getStoreCategory() throws Exception;

	Object getStoreImages() throws Exception;

	// 가게 상세 보기

	Object getDetailSchedule(int storeId) throws Exception;

	Object getDetailReserveSlots(int storeId) throws Exception;

	Object getDetailCoupon(int storeId) throws Exception;

	Object getDetailReview(int storeId) throws Exception;

	Object getDetailStore(int storeId) throws Exception;

	Object getDetailMenu(int storeId) throws Exception;

	Object getDetailFacility(int storeId) throws Exception;

	Object getDetailStoreImages(int storeId) throws Exception;

	// 필터링으로
	List<Integer> getStoreIdsByFilters(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

	List<MenuDTO> getMenusByStoreIds(List<Integer> storeIds) throws Exception;

	List<CouponDTO> getCouponsByStoreIds(List<Integer> storeIds) throws Exception;

	List<StoreDTO> getStoresByIds(List<Integer> storeIds) throws Exception;

	List<ScheduleDTO> getSchedulesByStoreIds(List<Integer> storeIds) throws Exception;

	List<StoreImagesDTO> getStoreImagesByStoreIds(List<Integer> storeIds) throws Exception;

	List<StoreCategoryDTO> getStoreCategoriesByStoreIds(List<Integer> storeIds) throws Exception;


}
