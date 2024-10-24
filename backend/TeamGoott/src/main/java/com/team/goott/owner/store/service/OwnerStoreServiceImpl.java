package com.team.goott.owner.store.service;

import java.util.ArrayList;
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
    	validateStoreDTO(store);
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
    
    // StoreDTO 유효성 검사 메서드
    private void validateStoreDTO(StoreDTO store) throws Exception {
        if (store.getRotationId() <= 0) {
            throw new Exception("필수 정보 오류: rotationId는 필수 항목 입니다.");
        }
        if (store.getStoreName() == null || store.getStoreName().isEmpty()) {
            throw new Exception("필수 정보 오류: storeName은 비어 있을 수 없습니다.");
        }
        if (store.getAddress() == null || store.getAddress().isEmpty()) {
            throw new Exception("필수 정보 오류: address는 비어 있을 수 없습니다.");
        }
        if (store.getTel() == null || store.getTel().isEmpty()) {
            throw new Exception("필수 정보 오류: 전화번호는 필수입니다.");
        }
        if (store.getMaxPeople() <= 0) {
            throw new Exception("필수 정보 오류: 최대 인원은 필수 입력사항입니다.");
        }
        if (store.getMaxPeoplePerReserve() <= 0) {
            throw new Exception("필수 정보 오류: maxPeoplePerReserve는 0보다 커야 합니다.");
        }
        if (store.getLocationLatX() == null) {
            throw new Exception("필수 정보 오류: locationLatX는 null일 수 없습니다.");
        }
        if (store.getLocationLonY() == null) {
            throw new Exception("필수 정보 오류: locationLonY는 null일 수 없습니다.");
        }
    }

    private void saveSchedules(List<ScheduleDTO> schedules, int storeId) throws Exception {
        for (ScheduleDTO schedule : schedules) {
            schedule.setStoreId(storeId);
            log.info("서비스단 schedule : {} ", schedule);

            Map<String, Object> scheduleMap = new HashMap<>();

            if (schedule.isCloseDay()) {
                // closeDay가 true인 경우: open과 close는 null이 아니어야 함
                if (schedule.getOpen() == null || schedule.getClose() == null) {
                    throw new Exception("일정 등록 실패: closeDay가 true일 때 open과 close는 null일 수 없습니다.");
                }
                scheduleMap.put("closeDay", 1);
            } else {
                scheduleMap.put("closeDay", 0);
            }

            scheduleMap.put("storeId", storeId);
            scheduleMap.put("dayCodeId", schedule.getDayCodeId());
            scheduleMap.put("open", schedule.getOpen());
            scheduleMap.put("close", schedule.getClose());

            // 데이터베이스에 스케줄 저장
            int result = ownerStoreDao.createSchedule(scheduleMap);
            if (result <= 0) {
                throw new Exception("일정 등록에 실패하였습니다.");
            }
        }
        log.info("모든 일정이 성공적으로 등록되었습니다.");
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

        // 가게 정보 업데이트
        int updatedCount = updateStoreInfo(storeId, store, storeData);
        
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

    private int updateStoreInfo(int storeId, StoreDTO store, StoreVO storeData) throws Exception {
        Map<String, Object> storeUpdateData = new HashMap<>();

        if (store.getStoreName() != null && !store.getStoreName().equals(storeData.getStoreName())) {
            storeUpdateData.put("storeName", store.getStoreName());
        }

        if (storeData.getRotationId() != store.getRotationId()) {
            storeUpdateData.put("rotationId", store.getRotationId());
        }

        if (store.getAddress() != null && !store.getAddress().equals(storeData.getAddress())) {
            storeUpdateData.put("address", store.getAddress());
        }

        if (store.getTel() != null && !store.getTel().equals(storeData.getTel())) {
            storeUpdateData.put("tel", store.getTel());
        }

        if (store.getDescription() != null && !store.getDescription().equals(storeData.getDescription())) {
            storeUpdateData.put("description", store.getDescription());
        }

        if (store.getDirectionGuide() != null && !store.getDirectionGuide().equals(storeData.getDirectionGuide())) {
            storeUpdateData.put("directionGuide", store.getDirectionGuide());
        }

        if (store.getMaxPeople() != 0 && storeData.getMaxPeople() != store.getMaxPeople()) {
            storeUpdateData.put("maxPeople", store.getMaxPeople());
        }

        if (store.getMaxPeoplePerReserve() != 0 && storeData.getMaxPeoplePerReserve() != store.getMaxPeoplePerReserve()) {
            storeUpdateData.put("maxPeoplePerReserve", store.getMaxPeoplePerReserve());
        }

        if (store.getLocationLatX() != null && !store.getLocationLatX().equals(storeData.getLocationLatX())) {
            storeUpdateData.put("locationLatX", store.getLocationLatX());
        }

        if (store.getLocationLonY() != null && !store.getLocationLonY().equals(storeData.getLocationLonY())) {
            storeUpdateData.put("locationLonY", store.getLocationLonY());
        }

        if (storeData.getSidoCodeId() != store.getSidoCodeId()) {
            storeUpdateData.put("sidoCodeId", store.getSidoCodeId());
        }

        log.info("서비스단 storeUpdateData : " + storeUpdateData.toString());

        return ownerStoreDao.updateStore(storeId, storeUpdateData);
    }

    private void updateSchedules(int storeId, List<ScheduleDTO> schedules, List<ScheduleVO> schedulesData) throws Exception {
        if (schedules != null && !schedules.isEmpty()) {
            for (ScheduleDTO newSchedule : schedules) {
                for (ScheduleVO scheduleData : schedulesData) {
                    if (scheduleData.getDayCodeId() == newSchedule.getDayCodeId()) {
                        Map<String, Object> scheduleUpdateData = new HashMap<>();

                        // 변경된 사항만 업데이트
                        if (!scheduleData.getOpen().equals(newSchedule.getOpen())) {
                            scheduleUpdateData.put("open", newSchedule.getOpen());
                        }
                        if (!scheduleData.getClose().equals(newSchedule.getClose())) {
                            scheduleUpdateData.put("close", newSchedule.getClose());
                        }
                        if (scheduleData.isCloseDay() != newSchedule.isCloseDay()) {
                            scheduleUpdateData.put("isCloseDay", newSchedule.isCloseDay());
                        }

                        // 스케줄 정보 업데이트
                        if (!scheduleUpdateData.isEmpty()) {
                            scheduleUpdateData.put("storeId", storeId);
                            scheduleUpdateData.put("dayCodeId", scheduleData.getDayCodeId());
                            ownerStoreDao.updateSchedule(storeId, scheduleUpdateData);
                        }
                        break;
                    }
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

    // 해당 가게의 storeId를 참조하는 storeImages 테이블의 사진 수가 5장이 넘을경우 예외처리 
    @Transactional(rollbackFor = Exception.class)
    private void processImages(int storeId, List<MultipartFile> updateFiles, List<String> deletedImages) throws Exception {
        // 파일 삭제 로직: 삭제 요청된 파일 처리
    	int storeImagesCount = ownerStoreDao.getStoreImagesCountByStoreId(storeId);
    	
    	if (storeImagesCount > 5) {
    		throw new Exception("가게 이미지는 최대 5장까지 등록 가능합니다");
    	}
    	
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

    private void deleteStoreImages(List<String> deleteFiles, int storeId) throws Exception {
        // 입력 파일 리스트가 비어있는지 체크
        if (deleteFiles == null || deleteFiles.isEmpty()) {
            log.warn("삭제할 파일 리스트가 비어 있습니다.");
            return; // 삭제할 파일이 없는 경우 메서드 종료
        }

        log.info("서비스단 delete 요청 파일 배열: " + deleteFiles.toString());
        
        // 데이터베이스에서 해당 storeId의 파일 목록 조회
        List<StoreImagesVO> existingFiles = ownerStoreDao.getStoreImagesByStoreId(storeId);

        // 실제로 존재하는 파일만 필터링
        List<String> filesToDelete = new ArrayList<>();
        for (String fileName : deleteFiles) {
            for (StoreImagesVO existingFile : existingFiles) {
                if (existingFile.getFileName().equals(fileName)) {
                    filesToDelete.add(fileName);
                    break; // 일치하는 파일이 발견되면 더 이상 확인할 필요 없음
                }
            }
        }

        // 삭제할 파일이 없는 경우 처리
        if (filesToDelete.isEmpty()) {
            log.warn("삭제할 파일이 데이터베이스에 존재하지 않습니다.");
            return;
        }
        
        // S3에서 이미지 삭제
        for (String fileName : deleteFiles) {
            S3ImageManager s3ImageManager = new S3ImageManager(s3Client, bucketName, fileName); // S3에서 이미지 삭제
            s3ImageManager.deleteImage(); // 실제로 이미지 삭제 메서드 호출
        }


        // Map 생성
        Map<String, List<String>> filesToDeleteMap = new HashMap<>();
        filesToDeleteMap.put("fileNames", deleteFiles);

        int deletedCount = ownerStoreDao.deleteStoreImagesByFileNames(storeId, filesToDeleteMap);
        
        // 삭제된 파일 개수 확인
        if (deletedCount == deleteFiles.size()) {
            log.info("모든 파일이 성공적으로 삭제되었습니다.");
        } else if (deletedCount > 0) {
            log.warn("일부 파일이 삭제되지 않았습니다. 삭제된 파일 수: " + deletedCount);
        } else {
            log.warn("삭제된 파일이 없습니다.");
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
