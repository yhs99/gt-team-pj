package com.team.goott.owner.store.persistence;

import com.team.goott.owner.domain.CategoryCodeDTO;
import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface OwnerStoreDAO {

	int createStore(StoreDTO store) throws Exception;

	int createSchedule(ScheduleDTO schedule) throws Exception;

	int createCategory(StoreCategoryDTO category) throws Exception;

	int createFacility(FacilityDTO facility) throws Exception;
	

}
