package com.team.goott.owner.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerStoreServiceImpl implements OwnerStoreService {
	
    @Autowired
    private OwnerStoreDAO ownerStoreDao;

    // 가게 생성
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int createStore(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category, FacilityDTO facility) throws Exception {
    	// store의 sidoCode를 설정해주는 메서드
        setSidoCodeId(store);

        log.info("서비스단 store : " + store.toString());
        
        // 가게 저장 로직
        int insertResult = ownerStoreDao.createStore(store); // insert 결과
        
        if (insertResult > 0) {
            log.info("생성된 storeId: " + store.getStoreId()); // 생성된 storeId
            
            for (ScheduleDTO schedule : schedules) {
                schedule.setStoreId(store.getStoreId()); // 각 일정에 storeId 설정
                ownerStoreDao.createSchedule(schedule); // 일정 저장
            }
            
            // 카테고리 저장
            category.setStoreId(store.getStoreId()); // storeId 설정
            
            if (ownerStoreDao.createCategory(category) > 0) {
                log.info("카테고리 저장 완료: " + category.getStoreCategoryName());
            } else {
                throw new Exception("카테고리 등록에 실패하였습니다."); // 실패 시 예외 처리
            }
            
            // facility 저장
            facility.setStoreId(store.getStoreId());
            
            if (ownerStoreDao.createFacility(facility) > 0) {
            	log.info("편의시설 정보 등록 완료 ");
            } else {
            	throw new Exception("편의시설 정보등록에 실패하였습니다.");
            }
            
            return 1; // 생성된 storeId 반환
        } else {
            throw new Exception("가게 등록에 실패하였습니다."); // 실패 시 예외 처리
        }
    }

	private void setSidoCodeId(StoreDTO store) {
		String sidoName = store.getSidoCode();
        
        // 시/도 이름에 따라 sidoCodeId를 설정
        switch (sidoName) {
            case "서울":
                store.setSidoCodeId(10);
                break;
            case "경기":
                store.setSidoCodeId(20);
                break;
            case "강원특별자치도":
                store.setSidoCodeId(30);
                break;
            case "충북":
                store.setSidoCodeId(40);
                break;
            case "충남":
                store.setSidoCodeId(50);
                break;
            case "전북특별자치도":
                store.setSidoCodeId(60);
                break;
            case "전남":
                store.setSidoCodeId(70);
                break;
            case "경북":
                store.setSidoCodeId(80);
                break;
            case "경남":
                store.setSidoCodeId(90);
                break;
            case "부산":
                store.setSidoCodeId(100);
                break;
            case "대구":
                store.setSidoCodeId(110);
                break;
            case "인천":
                store.setSidoCodeId(120);
                break;
            case "광주":
                store.setSidoCodeId(130);
                break;
            case "대전":
                store.setSidoCodeId(140);
                break;
            case "울산":
                store.setSidoCodeId(150);
                break;
            case "세종특별자치시":
                store.setSidoCodeId(160);
                break;
            case "제주특별자치도":
                store.setSidoCodeId(170);
                break;
            default:
                throw new IllegalArgumentException("유효한 지역을 입력해주세요 : " + sidoName);
        }
	}
}
