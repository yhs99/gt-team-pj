package com.team.goott.user.store.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.CouponDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.ScheduleDTO;
import com.team.goott.user.domain.StoreCategoryDTO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserStoreDAOImpl implements UserStoreDAO {

	@Inject
	private SqlSession ses;

	private static String ns = "com.team.mappers.user.store.userStoreMapper.";

	@Override
	public List<Object> getStoreById(int storeId) throws Exception {

		return ses.selectList(ns + "getStoreById", storeId);
	}

	// 모든 식당 보기
	@Override
	public Object getStore() throws Exception {
		return ses.selectList(ns + "getStore");
	}

	@Override
	public Object getFacility() throws Exception {
		return ses.selectList(ns + "getFacility");
	}

	@Override
	public Object getSchedule() throws Exception {
		return ses.selectList(ns + "getSchedule");
	}

	@Override
	public Object getSidocode() throws Exception {
		return ses.selectList(ns + "getSidocode");
	}

	@Override
	public Object getStoreCategory() throws Exception {
		return ses.selectList(ns + "getStoreCategory");
	}

	@Override
	public Object getStoreImages() throws Exception {
		return ses.selectList(ns + "getStoreImages");
	}
	// ---------------------가게 상세 보기-----------------------------

	@Override
	public Object getDetailMenu(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailMenu", storeId);
	}

	@Override
	public Object getDetailReserveSlots(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailReserveSlots", storeId);
	}

	@Override
	public Object getDetailSchedule(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailSchedule", storeId);
	}

	@Override
	public Object getDetailCoupon(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailCoupon", storeId);
	}

	@Override
	public Object getDetailReview(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailReview", storeId);
	}

	@Override
	public Object getDetailStore(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailStore", storeId);
	}

	@Override
	public Object getDetailFacility(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailFacility", storeId);
	}

	@Override
	public Object getDetailStoreImages(int storeId) throws Exception {
		return ses.selectList(ns + "getDetailStoreImages", storeId);
	}

	// 필터링으로 식당 정보
	@Override
	public List<MenuDTO> getMenusByStoreIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getMenusByStoreIds", storeIds);
	}

	@Override
	public List<CouponDTO> getCouponsByStoreIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getCouponsByStoreIds", storeIds);
	}

	@Override
	public List<StoreDTO> getStoresByIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getStoresByIds", storeIds);
	}

	@Override
	public List<ScheduleDTO> getSchedulesByStoreIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getSchedulesByStoreIds", storeIds);
	}

	@Override
	public List<StoreImagesDTO> getStoreImagesByStoreIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getStoreImagesByStoreIds", storeIds);
	}

	@Override
	public List<StoreCategoryDTO> getStoreCategoriesByStoreIds(List<Integer> storeIds) throws Exception {
		return ses.selectList(ns + "getStoreCategoriesByStoreIds", storeIds);
	}

	@Override
	public List<Integer> getStoreIdsByFilters(List<String> categoryCodeIds, List<Integer> sidoCodeIds)
			throws Exception {

		Map<String, Object> params = new HashMap<>();
	    // categoryCodeIds가 null이 아니고 비어있지 않을 경우에만 Map에 추가
	    if (categoryCodeIds != null && !categoryCodeIds.isEmpty()) {
	        params.put("categoryCodeIds", categoryCodeIds);
	    }

	    // sidoCodeIds가 null이 아니고 비어있지 않을 경우에만 Map에 추가
	    if (sidoCodeIds != null && !sidoCodeIds.isEmpty()) {
	        params.put("sidoCodeIds", sidoCodeIds);
	    }
		return ses.selectList(ns + "getStoreIdsByCategoryAndSido", params);
	}

}
