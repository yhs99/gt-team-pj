package com.team.goott.admin.store.service;

import java.util.List;

import com.team.goott.admin.domain.StoresVO;
import com.team.goott.infra.StoreNotFoundException;

public interface AdminStoreService {
	public List<StoresVO> getStoresInfo(List<String> categoryId, List<String> sidoCodeId, String searchParam, String showBlock);
	public int blockStore(int storeId) throws StoreNotFoundException;
	public int cancelBlock(int storeId) throws StoreNotFoundException;
}
