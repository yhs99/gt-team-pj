package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.OwnerDTO;
import java.util.List;
import java.util.Map;

import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreImagesVO;
import com.team.goott.owner.domain.StoreVO;

public interface OwnerStoreDAO {

	// store 테이블에 데이터 저장
	int createStore(StoreDTO store) throws Exception;
	StoreDTO login(String id, String pw);
	boolean ownerRegister(OwnerDTO ownerDTO);

	// schedule 테이블에 데이터 저장
	int createSchedule(Map<String, Object> scheduleMap);

	// category 테이블에 데이터 저장
	int createCategory(Map<String, Object> category) throws Exception;

	// facility 테이블에 데이터 저장
	int createFacility(Map<String, Object> facility) throws Exception;

	// storeImages 테이블에 데이터 저장
	int createStoreImages(StoreImagesDTO storeImages) throws Exception;

	// storeId로 가게 테이블 조회
	StoreVO getStoreById(int storeId) throws Exception;
	
	// storeId로 가게 운영시간 테이블 조회
	List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception;
	
	// storeId로 가게 카테고리 테이블 조회 
	List<StoreCategoryVO> getStoreCategoryByStoreId(int storeId) throws Exception;
	
	// storeId로 가게 편의시설 테이블 조회
	List<FacilityVO> getFacilityByStoreId(int storeId) throws Exception;
	
	// storeId로 가게 이미지 테이블 조회
	List<StoreImagesVO> getStoreImagesByStoreId(int storeId) throws Exception;

	// store 테이블 수정
	int updateStore(int storeId, Map<String, Object> store) throws Exception;
	
	// schedule 테이블 수정 (store 테이블이 수정될 때 트랙잭션 처리)
	void updateSchedule(int storeId, Map<String, Object> scheduleUpdateData) throws Exception;
	
	// 요청받은 imageName이 DB에 존재할 경우 이미지 삭제처리
	int deleteStoreImagesByFileNames(int storeId, Map<String, List<String>> filesToDeleteMap) throws Exception;

  // 이미지의 수를 가져오는 메서드
	int getStoreImagesCountByStoreId(int storeId) throws Exception;

	// 수정시 원래있던 데이터 삭제
	int deleteCategory(int storeId, String categoryCodeId) throws Exception;
	
	// 수정시 원래있던 데이터 삭제
	int deleteFacility(int storeId, String facilityDeleteData) throws Exception;

}
