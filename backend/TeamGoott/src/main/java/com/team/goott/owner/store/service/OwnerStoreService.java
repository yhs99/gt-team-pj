package com.team.goott.owner.store.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesVO;
import com.team.goott.owner.domain.StoreVO;

public interface OwnerStoreService {

	// 가게 등록 메서드
	int createStore(StoreDTO store, List<ScheduleDTO> schedules, List<StoreCategoryDTO> category, List<FacilityDTO> facility, List<MultipartFile> files) throws Exception;

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

	// 가게 정보를 수정하는 메서드
	int updateStore(int storeId, StoreDTO store, List<ScheduleDTO> schedules, List<StoreCategoryDTO> category,
			List<FacilityDTO> facility, List<MultipartFile> updatefiles, List<String> deleteImages) throws Exception;
	
	


}
