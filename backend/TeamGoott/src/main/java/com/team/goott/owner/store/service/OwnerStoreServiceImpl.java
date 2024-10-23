package com.team.goott.owner.store.service;

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
import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreImagesVO;
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

    // storeId로 store 테이블 정보를 조회하는 메서드 
    @Override
    public StoreVO getStoreById(int storeId) throws Exception {
        return ownerStoreDao.getStoreById(storeId);
    }
    
 // storeId로 schedule 테이블 정보를 조회하는 메서드 
    @Override
    public List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception {
        return ownerStoreDao.getSchedulesByStoreId(storeId);
    }

    // storeId로 storeCategory 테이블 정보를 조회하는 메서드 
    @Override
    public StoreCategoryVO getStoreCategoryByStoreId(int storeId) throws Exception {
        return ownerStoreDao.getStoreCategoryByStoreId(storeId);
    }

    // storeId로 facility 테이블 정보를 조회하는 메서드 
    @Override
    public FacilityVO getFacilityByStoreId(int storeId) throws Exception {
        return ownerStoreDao.getFacilityByStoreId(storeId);
    }

    // storeId로 storeImage 테이블 정보를 조회하는 메서드 
    @Override
    public List<StoreImagesVO> getStoreImagesByStoreId(int storeId) throws Exception {
        return ownerStoreDao.getStoreImagesByStoreId(storeId);
    }

    // 가게 정보를 업데이트하는 메서드
 // 가게 정보를 업데이트하는 메서드
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStore(int storeId, StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category,
                           FacilityDTO facility, List<MultipartFile> updateFiles, List<String> deletedImages) throws Exception {

    	log.info("서비스단 요청 테스트 : --------------------------------------");
    	
        setSidoCodeId(store); // sidoCodeId 설정
        
        log.info("서비스단 store : " + store.toString());

        // 기존 데이터 조회
        StoreVO storeData = ownerStoreDao.getStoreById(storeId);
        List<ScheduleVO> scheduleData = ownerStoreDao.getSchedulesByStoreId(storeId);
        StoreCategoryVO storeCategoryData = ownerStoreDao.getStoreCategoryByStoreId(storeId);
        FacilityVO facilityData = ownerStoreDao.getFacilityByStoreId(storeId);
        
        log.info("서비스단 : storeData : " + storeData);
        log.info("서비스단 : scheduleData : " + scheduleData);
        log.info("서비스단 : storeCategoryData : " + storeCategoryData);
        log.info("서비스단 : facilityData : " + facilityData);

        Map<String, Object> storeUpdateData = new HashMap<>();
        
        if (storeData.getStoreName() != null && !storeData.getStoreName().equals(store.getStoreName())) {
            storeUpdateData.put("storeName", store.getStoreName());
        }

        if (storeData.getRotationId() != store.getRotationId()) {
            storeUpdateData.put("rotationId", store.getRotationId());
        }

        if (storeData.getAddress() != null && !storeData.getAddress().equals(store.getAddress())) {
            storeUpdateData.put("address", store.getAddress());
        }

        if (storeData.getTel() != null && !storeData.getTel().equals(store.getTel())) {
            storeUpdateData.put("tel", store.getTel());
        }

        if (storeData.getDescription() != null && !storeData.getDescription().equals(store.getDescription())) {
            storeUpdateData.put("description", store.getDescription());
        }

        if (storeData.getDirectionGuide() != null && !storeData.getDirectionGuide().equals(store.getDirectionGuide())) {
            storeUpdateData.put("directionGuide", store.getDirectionGuide());
        }

        if (storeData.getMaxPeople() != store.getMaxPeople()) {
            storeUpdateData.put("maxPeople", store.getMaxPeople());
        }

        if (storeData.getMaxPeoplePerReserve() != store.getMaxPeoplePerReserve()) {
            storeUpdateData.put("maxPeoplePerReserve", store.getMaxPeoplePerReserve());
        }

        if (storeData.getLocationLatX() != null && !storeData.getLocationLatX().equals(store.getLocationLatX())) {
            storeUpdateData.put("locationLatX", store.getLocationLatX());
        }

        if (storeData.getLocationLonY() != null && !storeData.getLocationLonY().equals(store.getLocationLonY())) {
            storeUpdateData.put("locationLonY", store.getLocationLonY());
        }

        if (storeData.getSidoCodeId() != store.getSidoCodeId()) {
            storeUpdateData.put("sidoCodeId", store.getSidoCodeId());
        }
        log.info("서비스단 storeUpdateData : " +  storeUpdateData.toString());

        // 가게 정보 업데이트
        int updatedCount = updateStoreInfo(storeId, storeUpdateData);
        
        log.info("서비스단 updatedCount : " + updatedCount);

        // 일정 정보 업데이트
        updateSchedules(storeId, schedules, scheduleData);

        // 카테고리 정보 업데이트
        updateCategory(storeId, category, storeCategoryData);

        // 시설 정보 업데이트
        updateFacility(storeId, facility, facilityData);

        // 이미지 처리: 삭제 및 추가
        processImages(storeId, updateFiles, deletedImages);

        return updatedCount; // 업데이트된 가게 수 반환
    }

    private int updateStoreInfo(int storeId, Map<String, Object> storeUpdateData) throws Exception {
        return ownerStoreDao.updateStore(storeId, storeUpdateData);
    }

    private void updateSchedules(int storeId, List<ScheduleDTO> schedules, List<ScheduleVO> scheduleData) throws Exception {
        if (schedules != null && !schedules.isEmpty()) {
            // 일정 업데이트 로직
            for (ScheduleDTO newSchedule : schedules) {
                boolean found = false; // 기존 스케줄과 비교하기 위한 플래그
                
                for (ScheduleVO existingSchedule : scheduleData) {
                    if (existingSchedule.getDayCodeId() == newSchedule.getDayCodeId()) {
                        found = true;

                        // 기존 스케줄과 비교하여 변경된 사항만 업데이트
                        if (!existingSchedule.getOpen().equals(newSchedule.getOpen())) {
                            existingSchedule.setOpen(newSchedule.getOpen());
                        }
                        if (!existingSchedule.getClose().equals(newSchedule.getClose())) {
                            existingSchedule.setClose(newSchedule.getClose());
                        }
                        if (existingSchedule.isCloseDay() != newSchedule.isCloseDay()) {
                            existingSchedule.setCloseDay(newSchedule.isCloseDay());
                        }

                        // 업데이트 로직 수행
                        ownerStoreDao.updateSchedule(storeId, existingSchedule);
                        break; // 한 번 찾으면 내부 루프 종료
                    }
                }

                // 기존 스케줄에 없으면 추가하는 로직
                if (!found) {
                    ownerStoreDao.insertSchedule(storeId, newSchedule);
                }
            }
        }
    }

    private void updateCategory(int storeId, StoreCategoryDTO category, StoreCategoryVO storeCategoryData) throws Exception {
        if (category != null) {
            Map<String, Object> categoryUpdateData = new HashMap<>();

            // 카테고리 정보 비교 및 업데이트
            if (!storeCategoryData.getStoreCategoryName().equals(category.getStoreCategoryName())) {
                categoryUpdateData.put("storeCategoryName", category.getStoreCategoryName());
            }

            // 카테고리 정보 업데이트
            if (!categoryUpdateData.isEmpty()) {
                ownerStoreDao.updateCategory(storeId, categoryUpdateData);
            }
        }
    }

    private void updateFacility(int storeId, FacilityDTO facility, FacilityVO facilityData) throws Exception {
        if (facility != null) {
            Map<String, Object> facilityUpdateData = new HashMap<>();

            // 시설 정보 비교 및 업데이트
            if (!facilityData.getFacilityCode().equals(facility.getFacilityCode())) {
                facilityUpdateData.put("facilityCode", facility.getFacilityCode());
            }

            // 시설 정보 업데이트
            if (!facilityUpdateData.isEmpty()) {
                ownerStoreDao.updateFacility(storeId, facilityUpdateData);
            }
        }
    }

    private void processImages(int storeId, List<MultipartFile> updateFiles, List<String> deletedImages) throws Exception {
        // 파일 삭제 로직: 삭제 요청된 파일 처리
        if (deletedImages != null && !deletedImages.isEmpty()) {
            deleteStoreImages(deletedImages, storeId);
        }

        // 파일 처리 로직: 새로 추가된 파일 저장
        if (updateFiles != null && !updateFiles.isEmpty()) {
            saveStoreImages(updateFiles, storeId);
        }
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
            S3ImageManager s3ImageManager = new S3ImageManager(s3Client, bucketName, deleteFile);
            log.info("서비스 단 deleteFile : " + deleteFile);
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
