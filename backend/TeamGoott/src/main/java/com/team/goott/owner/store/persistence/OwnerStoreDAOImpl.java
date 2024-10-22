package com.team.goott.owner.store.persistence;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreVO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerStoreDAOImpl implements OwnerStoreDAO {
	
	@Autowired
	SqlSession ses;
	
	private String ns = "com.team.mappers.owner.store.ownerStoreMapper.";
	
	@Override
	
	// store 테이블에 데이터 저장
	public int createStore(StoreDTO store) throws Exception {
//		log.info(store.getOwnerId()+"");
		return ses.insert(ns + "createStore", store);
	}

	// schedule 테이블에 데이터 저장
	@Override
	public int createSchedule(ScheduleDTO schedule) throws Exception {
		return ses.insert(ns + "createSchedule", schedule);
	}

	// category 테이블에 데이터 저장
	@Override
	public int createCategory(StoreCategoryDTO category) throws Exception {
		return ses.insert(ns + "createCategory", category);
	}

	// facility 테이블에 데이터 저장
	@Override
	public int createFacility(FacilityDTO facility) throws Exception {
		return ses.insert(ns + "createFacility", facility);
	}
	
	// storeImages 테이블에 데이터 저장
	@Override
	public int createStoreImages(StoreImagesDTO storeImages) throws Exception {
		return ses.insert(ns + "createStoreImages", storeImages);
	}

	// storeId로 store테이블의 정보를 가져오기
	@Override
	public StoreVO getStoreById(int storeId) throws Exception {
		return ses.selectOne(ns + "getStoreById", storeId);
	}

	// storeId를 이용해 store 테이블의 정보를 수정
    @Override
    public int updateStore(int storeId, StoreDTO store) throws Exception {
        store.setStoreId(storeId); // StoreDTO에 storeId 설정
        return ses.update(ns + "updateStore", store); // SQL 쿼리 호출
    }
    
    // storeId를 이용해 schedule 테이블의 정보를 수정
    @Override
    public void updateSchedule(int storeId, ScheduleDTO schedule) throws Exception {
        ses.update(ns + "updateSchedule", schedule);
    }

    // storeId를 이용해 storeCategory 테이블의 정보를 수정
    @Override
    public void updateCategory(int storeId, StoreCategoryDTO category) throws Exception {
        ses.update(ns + "updateCategory", category);
    }

    // storeId를 이용해 facility 테이블을 수정
    @Override
    public void updateFacility(int storeId, FacilityDTO facility) throws Exception {
        ses.update(ns + "updateFacility", facility);
    }
    
    // 삭제 요청받은 fileName을 이용하여 storeId를 참조하는 storeImages의 테이블을 수정
    public int deleteStoreImages(int storeId, String deleteFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        params.put("fileName", deleteFile);
        return ses.delete(ns + "deleteStoreImage", params);
    }

}
