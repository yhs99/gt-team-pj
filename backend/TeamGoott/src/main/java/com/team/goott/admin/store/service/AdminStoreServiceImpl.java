package com.team.goott.admin.store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.store.persistence.AdminStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminStoreServiceImpl implements AdminStoreService {
	
	@Autowired
	private AdminStoreDAO dao;
	
	@Override
	public List<StoresVO> getStoresInfo(List<String> categoryId, List<String> sidoCodeId, String searchParam) {
		Map<String, Object> searchQuery = new HashMap<String, Object>();
		searchQuery.put("categoryId", categoryId);
		searchQuery.put("sidoCodeId", sidoCodeId);
		searchQuery.put("searchParam", searchParam);
		log.info("{}",searchQuery.toString());
		List<StoresVO> stores = dao.getStoresInfo(searchQuery);
		log.info(stores.toString());
		return stores;
	}

	@Override
	public int getStoresInfoCnt(List<String> categoryId, List<String> sidoCodeId, String searchParam) {
		Map<String, Object> searchQuery = new HashMap<String, Object>();
		searchQuery.put("categoryId", categoryId);
		searchQuery.put("sidoCodeId", sidoCodeId);
		searchQuery.put("searchParam", searchParam);
		return dao.getStoresInfoCnt(searchQuery);
	}
	
}
