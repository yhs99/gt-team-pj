package com.team.goott.user.store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.CouponDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.ScheduleDTO;
import com.team.goott.user.domain.StoreCategoryDTO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreImagesDTO;
import com.team.goott.user.store.persistence.UserStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreServiceImpl implements UserStoreService {
	
	@Inject
	private UserStoreDAO userStoreDAO;
	
	@Override
	public Map<String, Object> getAllStores() throws Exception {
		Map<String, Object> result = new HashMap<>();
		result.put("stores", userStoreDAO.getStore());
		result.put("facility", userStoreDAO.getFacility());
		result.put("schedule", userStoreDAO.getSchedule());
		result.put("sidocode", userStoreDAO.getSidocode());
		result.put("storeCategory", userStoreDAO.getStoreCategory());
		result.put("storeImages", userStoreDAO.getStoreImages());
		
		return result;
	}
	@Override
	public Map<String, Object> getDetailsStoreById(int storeId) throws Exception {
		Map<String, Object> result = new HashMap<>();
		result.put("store", userStoreDAO.getDetailStore(storeId));
		result.put("menu", userStoreDAO.getDetailMenu(storeId));
		result.put("schedule", userStoreDAO.getDetailSchedule(storeId));
		result.put("reserveSlots", userStoreDAO.getDetailReserveSlots(storeId));
		result.put("coupon", userStoreDAO.getDetailCoupon(storeId));
		result.put("review", userStoreDAO.getDetailReview(storeId));
		result.put("facility", userStoreDAO.getDetailFacility(storeId));
		result.put("storeImages", userStoreDAO.getDetailStoreImages(storeId));
		
		return result;
	}


	@Override
	public List<Object> getStoreById(int storeId) throws Exception {
		return userStoreDAO.getStoreById(storeId);
	}

	@Override
	public Map<String, Object> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception{
		List<Integer> storeIds = userStoreDAO.getStoreIdsByFilters(categoryCodeIds, sidoCodeIds);
		
		if(storeIds == null || storeIds.isEmpty()) {
			return new HashMap<>();
		}
		
		
        // Step 2: 해당 스토어 ID에 맞는 메뉴, 쿠폰, 스토어, 스케줄, 이미지, 카테고리 정보 가져오기
        List<MenuDTO> menus = userStoreDAO.getMenusByStoreIds(storeIds);
        List<CouponDTO> coupons = userStoreDAO.getCouponsByStoreIds(storeIds);
        List<StoreDTO> stores = userStoreDAO.getStoresByIds(storeIds);
        List<ScheduleDTO> schedules = userStoreDAO.getSchedulesByStoreIds(storeIds);
        List<StoreImagesDTO> storeImages = userStoreDAO.getStoreImagesByStoreIds(storeIds);
        List<StoreCategoryDTO> storeCategories = userStoreDAO.getStoreCategoriesByStoreIds(storeIds);

        // Step 3: 결과를 맵으로 저장하여 반환
        Map<String, Object> result = new HashMap<>();
        result.put("stores", stores);
        result.put("menus", menus);
        result.put("coupons", coupons);
        result.put("schedules", schedules);
        result.put("storeImages", storeImages);
        result.put("storeCategories", storeCategories);

        return result;
    }
}
