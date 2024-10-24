package com.team.goott.owner.store.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreImagesVO;
import com.team.goott.owner.domain.StoreVO;

public interface OwnerStoreDAO {

	// store 테이블에 데이터 저장
	int createStore(StoreDTO store) throws Exception;

	// schedule 테이블에 데이터 저장
	int createSchedule(Map<String, Object> scheduleMap);

	// category 테이블에 데이터 저장
	int createCategory(StoreCategoryDTO category) throws Exception;

	// facility 테이블에 데이터 저장
	int createFacility(FacilityDTO facility) throws Exception;

	// storeImages 테이블에 데이터 저장
	int createStoreImages(StoreImagesDTO storeImages) throws Exception;

	// storeId로 가게 테이블 조회
	StoreVO getStoreById(int storeId) throws Exception;
	
	// storeId로 가게 운영시간 테이블 조회
    List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 카테고리 테이블 조회 
    StoreCategoryVO getStoreCategoryByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 편의시설 테이블 조회
    FacilityVO getFacilityByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 이미지 테이블 조회
    List<StoreImagesVO> getStoreImagesByStoreId(int storeId) throws Exception;

	// store 테이블 수정
	int updateStore(int storeId, Map<String, Object> store) throws Exception;
	
	// schedule 테이블 수정 (store 테이블이 수정될 때 트랙잭션 처리)
    void updateSchedule(int storeId, Map<String, Object> scheduleUpdateData) throws Exception;
    
    // category 테이블 수정 (store 테이블이 수정될 때 트랙잭션 처리)
    void updateCategory(int storeId, Map<String, Object> categoryUpdateData) throws Exception;

	// facility 테이블 수정 (store 테이블이 수정될 때 트랙잭션 처리)
    void updateFacility(int storeId, Map<String, Object> facilityUpdateData) throws Exception;
    
    // 요청받은 이미지 파일을 storeId를 참조하여 storeImages 테이블의 이미지를 삭제
    int deleteStoreImagesByFileNames(int storeId, Map<String, List<String>> filesToDeleteMap) throws Exception;

    // 이미지의 수를 가져오는 메서드
	int getStoreImagesCountByStoreId(int storeId) throws Exception;
	
	

}
