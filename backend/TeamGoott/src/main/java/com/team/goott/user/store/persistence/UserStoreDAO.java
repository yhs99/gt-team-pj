package com.team.goott.user.store.persistence;

import java.util.List;

import com.team.goott.owner.domain.CategoryCodeVO;
import com.team.goott.owner.domain.FacilityCodeVO;
import com.team.goott.owner.domain.sidoCodeVO;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.domain.StoreFiltersVO;

public interface UserStoreDAO {

	List<StoreDTO> getAllStores() throws Exception;

	List<Object> getStoreById(int storeId) throws Exception;

	List<StoreDTO> getStoresByCategoriesAndSidos(List<String> categoryCodeIds, List<Integer> sidoCodeIds) throws Exception;

	List<CategoryCodeVO> getStoreFilterCategory();

	List<sidoCodeVO> getStoreFiltersSidoCode();

	List<FacilityCodeVO> getStoreFiltersFacilityCode();
}
