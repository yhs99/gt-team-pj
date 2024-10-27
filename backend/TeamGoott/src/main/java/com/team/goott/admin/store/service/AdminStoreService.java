package com.team.goott.admin.store.service;

import java.util.List;

import com.team.goott.admin.domain.StoresVO;

public interface AdminStoreService {
	public List<StoresVO> getStoresInfo(List<String> categoryId, List<String> sidoCodeId, String searchParam);
	public int getStoresInfoCnt(List<String> categoryId, List<String> sidoCodeId, String searchParam);
}
