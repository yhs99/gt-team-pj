package com.team.goott.owner.store.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	
    @Override
    public List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception {
        return ses.selectList(ns + "getSchedulesByStoreId", storeId);
    }

    @Override
    public StoreCategoryVO getStoreCategoryByStoreId(int storeId) throws Exception {
        return ses.selectOne(ns + "getStoreCategoryByStoreId", storeId);
    }

    @Override
    public FacilityVO getFacilityByStoreId(int storeId) throws Exception {
        return ses.selectOne(ns + "getFacilityByStoreId", storeId);
    }

    @Override
    public List<StoreImagesVO> getStoreImagesByStoreId(int storeId) throws Exception {
        return ses.selectList(ns + "getStoreImagesByStoreId", storeId);
    }
	// storeId를 이용해 store 테이블의 정보를 수정
    @Override
    public int updateStore(int storeId, Map<String, Object> storeUpdateData) throws Exception {
    	storeUpdateData.put("storeId", storeId);
        return ses.update(ns + "updateStore", storeUpdateData); // SQL 쿼리 호출
    }
    
    @Override
    public void updateSchedule(int storeId, ScheduleVO schedule) throws Exception {
        schedule.setStoreId(storeId);
        ses.update(ns + "updateSchedule", schedule);
    }

    @Override
    public void insertSchedule(int storeId, ScheduleDTO schedule) throws Exception {
        schedule.setStoreId(storeId);
        ses.insert(ns + "insertSchedule", schedule);
    }

    @Override
    public void updateCategory(int storeId, Map<String, Object> categoryUpdateData) throws Exception {
        categoryUpdateData.put("storeId", storeId);
        ses.update(ns + "updateCategory", categoryUpdateData);
    }

    @Override
    public void updateFacility(int storeId, Map<String, Object> facilityUpdateData) throws Exception {
        facilityUpdateData.put("storeId", storeId);
        ses.update(ns + "updateFacility", facilityUpdateData);
    }

    @Override
    public int deleteStoreImages(int storeId, String imageUrl) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        params.put("imageUrl", imageUrl);
        return ses.delete("OwnerStoreMapper.deleteImage", params);
    }


}
