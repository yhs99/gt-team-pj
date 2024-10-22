package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;

public interface OwnerStoreDAO {

	// store 테이블에 데이터 저장
	int createStore(StoreDTO store) throws Exception;

	// schedule 테이블에 데이터 저장
	int createSchedule(ScheduleDTO schedule) throws Exception;

	// category 테이블에 데이터 저장
	int createCategory(StoreCategoryDTO category) throws Exception;

	// facility 테이블에 데이터 저장
	int createFacility(FacilityDTO facility) throws Exception;

	// storeImages 테이블에 데이터 저장
	int createStoreImages(StoreImagesDTO storeImages) throws Exception;
	

}
