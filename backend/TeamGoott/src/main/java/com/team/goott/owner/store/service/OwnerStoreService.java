package com.team.goott.owner.store.service;

import java.util.List;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreService {

	int createStore(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category, FacilityDTO facility) throws Exception ;


}
