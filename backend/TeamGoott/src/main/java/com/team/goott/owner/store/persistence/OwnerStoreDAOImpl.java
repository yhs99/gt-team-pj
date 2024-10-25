package com.team.goott.owner.store.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.ScheduleVO;
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
    public int createSchedule(Map<String, Object> scheduleMap) {
        return ses.insert(ns + "createSchedule", scheduleMap);
    }

	// category 테이블에 데이터 저장
	@Override
	public int createCategory(Map<String, Object> category) throws Exception {
		return ses.insert(ns + "createCategory", category);
	}

	// facility 테이블에 데이터 저장
	@Override
	public int createFacility(Map<String, Object> facility) throws Exception {
		return ses.insert(ns + "createFacility", facility);
	}
	
	// storeImages 테이블에 데이터 저장
	@Override
	public int createStoreImages(StoreImagesDTO storeImages) throws Exception {
		return ses.insert(ns + "createStoreImages", storeImages);
	}

	// storeId로 store 테이블의 정보를 가져오기
	@Override
	public StoreVO getStoreById(int storeId) throws Exception {
		return ses.selectOne(ns + "getStoreById", storeId);
	}
	
	// storeId로 schedule 테이블의 정보를 가져오기
    @Override
    public List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception {
        return ses.selectList(ns + "getSchedulesByStoreId", storeId);
    }

    // storeId로 category 테이블의 정보를 가져오기
    @Override
    public List<StoreCategoryVO> getStoreCategoryByStoreId(int storeId) throws Exception {
        return ses.selectList(ns + "getStoreCategoryByStoreId", storeId);
    }

    // storeId로 facility 테이블의 정보를 가져오기
    @Override
    public List<FacilityVO> getFacilityByStoreId(int storeId) throws Exception {
        return ses.selectList(ns + "getFacilityByStoreId", storeId);
    }

    // storeId로 storeImages 테이블의 정보를 가져오기
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
    
    // storeId를 참조하는 schedule 테이블 수정
    @Override
    public void updateSchedule(int storeId,  Map<String, Object> scheduleUpdateData) throws Exception {
    	scheduleUpdateData.put("storeId", storeId);
        ses.update(ns + "updateSchedule", scheduleUpdateData);
    }

    // storeId를 참조하는 storeImages 테이블의 fileName, storeId를 이용하여 삭제 요청 받은 파일을 삭제
    @Override
    public int deleteStoreImagesByFileNames(int storeId, Map<String, List<String>> filesToDeleteMap) throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        params.put("fileNames", filesToDeleteMap.get("fileNames"));
        return ses.delete(ns + "deleteStoreImagesByFileNames", params);
    }

    // 해당 가게의 사진이 5장 이상 저장되지 않게 storeImages 테이블의 image 수를 가져옴
	@Override
	public int getStoreImagesCountByStoreId(int storeId) throws Exception {
		return ses.selectOne(ns+ "selectStoreImagesCountByStoreId", storeId);
	}
	
	// put 요청 들어왔을 때 기존 db에 없는 값이 요청되면 db에서 요청받지 못한 값들을 삭제
	@Override
	public int deleteCategory(int storeId, String categoryCodeId) throws Exception {
	    // 파라미터를 담을 Map 객체 생성
	    Map<String, Object> params = new HashMap<>();
	    params.put("storeId", storeId);
	    params.put("categoryCodeId", categoryCodeId);
	    params.put("storeCategoryName", categoryCodeId);

	    return ses.delete(ns + "deleteCategory", params);
	}
	
	// put 요청 들어왔을 때 기존 db에 없는 값이 요청되면 db에서 요청받지 못한 값들을 삭제
    @Override
    public int deleteFacility(int storeId, String facilityDeleteData) throws Exception {
    	Map<String, Object> params = new HashMap<>();
    	params.put("storeId", storeId);
    	params.put("facilityCode", facilityDeleteData);
    	
        return ses.delete(ns + "deleteFacility", params);
    }

}
