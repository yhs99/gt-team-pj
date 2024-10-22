package com.team.goott.owner.store.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreVO;

public interface OwnerStoreService {

	// 가게 등록 메서드
	int createStore(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category, FacilityDTO facility, List<MultipartFile> files) throws Exception;

	// storeId로 가게 정보 조회
	StoreVO getStoreById(int storeId) throws Exception;

	// 가게 정보를 수정하는 메서드
	int updateStore(int storeId, StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category,
			FacilityDTO facility, List<MultipartFile> updatefiles, List<String> deleteImages) throws Exception;
	
	


}
