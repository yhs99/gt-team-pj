package com.team.goott.user.store.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.CategoryCodeVO;
import com.team.goott.owner.domain.FacilityCodeVO;
import com.team.goott.owner.domain.sidoCodeVO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreFiltersVO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserStoreDAOImpl implements UserStoreDAO {
	
	@Inject
	private SqlSession ses;
	
	
	private static String ns = "com.team.mappers.user.store.userStoreMapper.";
	
	
	@Override
	public List<StoreDTO> getAllStores() throws Exception {
		return ses.selectList(ns+"getAllStores");
	}


	@Override
	public List<Object> getStoreById(int storeId) throws Exception {
		
		return ses.selectList(ns+"getStoreById",storeId);
	}


	@Override
	public List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("categoryCodeIds", categoryCodeIds);
		params.put("sidoCodeIds", sidoCodeIds);
		return ses.selectList(ns+"getStoresByCategory",params);
	}


	@Override
	public List<CategoryCodeVO> getStoreFilterCategory() {
		return ses.selectList(ns+"getStoreFiltersCategory");
	}
	
	@Override
	public List<sidoCodeVO> getStoreFiltersSidoCode() {
		return ses.selectList(ns+"getStoreFiltersSidoCode");
	}
	
	@Override
	public List<FacilityCodeVO> getStoreFiltersFacilityCode() {
		return ses.selectList(ns+"getStoreFiltersFacilityCode");
	}

}
