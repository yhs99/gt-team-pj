package com.team.goott.owner.store.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerStoreServiceImpl implements OwnerStoreService {

	@Inject
	private AmazonS3 s3Client;

	@Autowired
	private String bucketName;

	@Autowired
	private OwnerStoreDAO ownerStoreDao;

	// 모든 테이블에 정보가 insert 되었을 때 실행
	@Transactional(rollbackFor = Exception.class)
	@Override
    public int createStore(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category, FacilityDTO facility, List<MultipartFile> files) throws Exception {
        setSidoCodeId(store);
        log.info("서비스단 store : {}", store);

        // store테이블에 정보가 insert 되었을 경우 1이 반환되고, storeId를 반환값으로 받음
        if (ownerStoreDao.createStore(store) <= 0) {
            throw new Exception("가게 등록에 실패하였습니다.");
        }
        
        // 반환받은 storeId 값
        log.info("생성된 storeId: {}", store.getStoreId());
        
        // store가 생성되며 storeId를 반환받고, 그 storeId값을 참조로하는 테이블에 set 해줌

        // 운영시간 등록
        saveSchedules(schedules, store.getStoreId());
        
        // 카테고리 등록
        saveCategory(category, store.getStoreId());
        
        // 편의시설 등록
        saveFacility(facility, store.getStoreId());
        
        // 가게 이미지 등록 (최대 5장)
        saveStoreImages(files, store.getStoreId());

        return 1; 
    }

	private void saveSchedules(List<ScheduleDTO> schedules, int storeId) throws Exception {
	    for (ScheduleDTO schedule : schedules) {
	        schedule.setStoreId(storeId);
	        if (ownerStoreDao.createSchedule(schedule) <= 0) {
	            throw new Exception("일정 등록에 실패하였습니다."); 
	        }
	    }
    }

    private void saveCategory(StoreCategoryDTO category, int storeId) throws Exception {
        category.setStoreId(storeId);
        if (ownerStoreDao.createCategory(category) <= 0) {
            throw new Exception("카테고리 등록에 실패하였습니다.");
        }
        log.info("카테고리 저장 완료: {}", category.getStoreCategoryName());
    }

    private void saveFacility(FacilityDTO facility, int storeId) throws Exception {
        facility.setStoreId(storeId);
        if (ownerStoreDao.createFacility(facility) <= 0) {
            throw new Exception("편의시설 정보 등록에 실패하였습니다.");
        }
        log.info("편의시설 정보 등록 완료");
    }

	// storeId로 가게 정보를 조회하는 메서드 (수정시)
    @Override
    public StoreVO getStoreById(int storeId) throws Exception {
        return ownerStoreDao.getStoreById(storeId);
    }
    
    // 가게 정보를 업데이트하는 메서드
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStore(int storeId, StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category,
			FacilityDTO facility, List<MultipartFile> updateFiles, List<String> deletedImages) throws Exception {
    	setSidoCodeId(store);
        // 가게 정보 업데이트
        int updatedCount = ownerStoreDao.updateStore(storeId, store);
        
        // 일정 정보 업데이트
        for (ScheduleDTO schedule : schedules) {
            ownerStoreDao.updateSchedule(storeId, schedule);
        }

        // 카테고리 정보 업데이트
        if (category != null) {
            ownerStoreDao.updateCategory(storeId, category);
        }

        // 시설 정보 업데이트
        if (facility != null) {
            ownerStoreDao.updateFacility(storeId, facility);
        }

        // 파일 삭제 로직: 삭제 요청된 파일 처리
        if (deletedImages != null && !deletedImages.isEmpty()) {
        	deleteStoreImages(deletedImages, storeId);
        }

        // 파일 처리 로직: 새로 추가된 파일 저장
        if (updateFiles != null && !updateFiles.isEmpty()) {
            saveStoreImages(updateFiles, storeId);
        }

        return updatedCount; // 업데이트된 가게 수 반환
    }
    
    // 파일을 저장하는 메서드
    private void saveStoreImages(List<MultipartFile> files, int storeId) throws Exception {
        // 파일 개수가 5개를 초과하는 경우 예외 발생
        if (files.size() > 5) {
            throw new Exception("이미지는 최대 5장까지만 업로드 가능합니다.");
        }

        for (MultipartFile file : files) {
            S3ImageManager s3ImageManager = new S3ImageManager(file, s3Client, bucketName);
            Map<String, String> uploadImageInfo = s3ImageManager.uploadImage();

            StoreImagesDTO storeImages = new StoreImagesDTO();
            storeImages.setStoreId(storeId);
            storeImages.setUrl(uploadImageInfo.get("imageUrl"));
            storeImages.setFileName(uploadImageInfo.get("imageFileName"));

            if (ownerStoreDao.createStoreImages(storeImages) <= 0) {
                throw new Exception("가게 이미지 저장 실패");
            }
            log.info("가게 이미지 저장 완료: {}", uploadImageInfo.get("imageFileName"));
        }
    }
    
    // 파일을 삭제하는 메서드
    private void deleteStoreImages(List<String> deleteFiles, int storeId) throws Exception {
        for (String deleteFile : deleteFiles) {
            
            // 인스턴스 생성
            S3ImageManager s3ImageManager = new S3ImageManager(s3Client, bucketName, deleteFile);
            
            log.info("서비스 단 deleteFile : " +  deleteFile);
            // 파일 삭제
            s3ImageManager.deleteImage();

            // 데이터베이스에서 이미지 정보 삭제
            if (ownerStoreDao.deleteStoreImages(storeId, deleteFile) <= 0) {
                throw new Exception("가게 이미지 삭제 실패");
            }
            log.info("가게 이미지 삭제 완료: {}", deleteFile);
        }
    }
    
    // 입력받은 sido 이름으로 sidoCode값을 설정해주는 메서드
    private void setSidoCodeId(StoreDTO store) {
        Map<String, Integer> sidoCodeMap = new HashMap<>();
        sidoCodeMap.put("서울", 10);
        sidoCodeMap.put("경기", 20);
        sidoCodeMap.put("강원특별자치도", 30);
        sidoCodeMap.put("충북", 40);
        sidoCodeMap.put("충남", 50);
        sidoCodeMap.put("전북특별자치도", 60);
        sidoCodeMap.put("전남", 70);
        sidoCodeMap.put("경북", 80);
        sidoCodeMap.put("경남", 90);
        sidoCodeMap.put("부산", 100);
        sidoCodeMap.put("대구", 110);
        sidoCodeMap.put("인천", 120);
        sidoCodeMap.put("광주", 130);
        sidoCodeMap.put("대전", 140);
        sidoCodeMap.put("울산", 150);
        sidoCodeMap.put("세종특별자치시", 160);
        sidoCodeMap.put("제주특별자치도", 170);

        Integer sidoCodeId = sidoCodeMap.get(store.getSidoCode());
        if (sidoCodeId != null) {
            store.setSidoCodeId(sidoCodeId);
        } else {
            throw new IllegalArgumentException("유효한 지역을 입력해주세요: " + store.getSidoCode());
        }
    }
    
}
