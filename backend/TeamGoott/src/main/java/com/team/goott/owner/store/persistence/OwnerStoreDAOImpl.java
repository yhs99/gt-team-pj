package com.team.goott.owner.store.persistence;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.CategoryCodeDTO;
import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerStoreDAOImpl implements OwnerStoreDAO {
	
	@Autowired
	SqlSession ses;
	
	private String ns = "com.team.mappers.owner.store.ownerStoreMapper.";
	
	@Override
	
	public int createStore(StoreDTO store) throws Exception {
//		log.info(store.getOwnerId()+"");
		return ses.insert(ns + "createStore", store);
	}

	@Override
	public int createSchedule(ScheduleDTO schedule) throws Exception {
		return ses.insert(ns + "createSchedule", schedule);
	}

	@Override
	public int createCategory(StoreCategoryDTO category) throws Exception {
		return ses.insert(ns + "createCategory", category);
	}

	@Override
	public int createFacility(FacilityDTO facility) throws Exception {
		return ses.insert(ns + "createFacility", facility);
	}


}
