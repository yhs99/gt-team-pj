package com.team.goott.owner.store.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreService {

	// 가게 등록 메서드
	int createStore(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category, FacilityDTO facility, List<MultipartFile> files) throws Exception ;
	
	


}
