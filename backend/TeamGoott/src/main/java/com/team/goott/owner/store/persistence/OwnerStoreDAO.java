package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreVO;

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

	// storeId로 가게 정보 조회
	StoreVO getStoreById(int storeId) throws Exception;

	// store 테이블 수정
	int updateStore(int storeId, StoreDTO store) throws Exception;
	
	// schedule 테이블을 storeId를 참조하여 수정
    void updateSchedule(int StoreId, ScheduleDTO schedule) throws Exception; 
    
    // category 테이블을 storeId를 참조하여 수정
    void updateCategory(int StoreId, StoreCategoryDTO category) throws Exception; 
    
    // facility 테이블을 storeId를 참조하여 수정
    void updateFacility(int StoreId, FacilityDTO facility) throws Exception;

    // 요청받은 이미지 파일을 storeId를 참조하여 storeImages 테이블의 이미지를 삭제
	int deleteStoreImages(int storeId, String deleteFile) throws Exception;
	

}
