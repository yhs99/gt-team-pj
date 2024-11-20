package com.team.goott.admin.store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.domain.SummaryDTO;
import com.team.goott.admin.domain.SummaryTitleDTO;
import com.team.goott.admin.domain.SummaryVO;
import com.team.goott.admin.store.persistence.AdminStoreDAO;
import com.team.goott.infra.StoreNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminStoreServiceImpl implements AdminStoreService {
	
	@Autowired
	private AdminStoreDAO dao;
	
	@Override
	public List<StoresVO> getStoresInfo(List<String> categoryId, List<String> sidoCodeId, String searchParam, String showBlock) {
		Map<String, Object> searchQuery = new HashMap<String, Object>();
		searchQuery.put("categoryId", categoryId);
		searchQuery.put("sidoCodeId", sidoCodeId);
		searchQuery.put("searchParam", searchParam);
		searchQuery.put("showBlock", showBlock);
		List<StoresVO> stores = dao.getStoresInfo(searchQuery);
		return stores;
	}

	@Override
	public int blockStore(int storeId) throws StoreNotFoundException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("storeId", storeId);
		map.put("isBlocked", 0);
		int isValidStore = dao.isExistStore(map);
		log.info("storeId {} 결과 개수 :: {}", storeId, isValidStore);
		if(isValidStore != 1) {
			throw new StoreNotFoundException("유효하지 않은 매장입니다.");
		}else {
			return dao.blockStore(storeId);
		}
	}

	@Override
	public int cancelBlock(int storeId) throws StoreNotFoundException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("storeId", storeId);
		map.put("isBlocked", 1);
		int isValidStore = dao.isExistStore(map);
		log.info("storeId {} 결과 개수 :: {}", storeId, isValidStore);
		if(isValidStore != 1) {
			throw new StoreNotFoundException("유효하지 않은 매장입니다.");
		}else {
			return dao.cancelBlock(storeId);
		}
	}

	@Override
	public SummaryDTO getSummary(int storeId) {
		SummaryTitleDTO summaryTitle = dao.getSummaryTitle(storeId);
		List<SummaryVO> dailySales = dao.getDailySales(storeId);
		List<SummaryVO> monthlySales = dao.getMonthlySales(storeId);
		return SummaryDTO.builder()
				.summaryTitle(summaryTitle)
				.dailySales(dailySales)
				.monthlySales(monthlySales)
				.build();
	}

	@Override
	public StoresVO getStoreInfoForUpdate(int storeId) {
		Map<String, Object> searchQuery = new HashMap<String, Object>();
		searchQuery.put("categoryId", null);
		searchQuery.put("sidoCodeId", null);
		searchQuery.put("searchParam", "");
		searchQuery.put("showBlock", "");
		searchQuery.put("storeId", storeId);
		return dao.getStoreInfoForUpdate(searchQuery);
	}
	
}
