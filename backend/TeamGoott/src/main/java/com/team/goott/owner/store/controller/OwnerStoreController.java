package com.team.goott.owner.store.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.OwnerOnly;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesVO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;
import com.team.goott.owner.store.service.OwnerStoreService;


import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/store")
public class OwnerStoreController {

	@Autowired
	private OwnerStoreService ownerStoreService;
	
	@Autowired
	private OwnerStoreDAO ownerStoreDao;
	
	// 점주 가입
	@PostMapping("/owner/register")
	public ResponseEntity<Object> userLoginRequest(OwnerDTO ownerDTO){
		boolean result = false;
		try {
			result = ownerStoreService.register(ownerDTO);
		} catch (DuplicateKeyException | MyBatisSystemException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디 입니다 ");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생 ");
		}
		return ResponseEntity.ok(result?"점주 회원가입이 완료되었습니다. ":"실패");
	}
	
	@OwnerOnly
	@GetMapping("")
	public ResponseEntity<Object> getStore(HttpSession session) {
	     
	     int ownerId = getOwnerIdFromSession(session).getOwnerId();
	     int storeId = getOwnerIdFromSession(session).getStoreId();
	     log.info("세션의 onwerId : {}"  , ownerId);
	    
	    StoreVO storeData = null;
	    List<ScheduleVO> scheduleData = null; 
	    List<StoreCategoryVO> storeCategoryData = null;
	    List<FacilityVO> facilityData = null; 
	    List<StoreImagesVO> storeImageData = null;
	    
	    try {
	        // 주어진 storeId에 대한 가게 정보 조회
	        storeData = ownerStoreDao.getStoreById(storeId);
	        scheduleData = ownerStoreDao.getSchedulesByStoreId(storeId);
	        storeCategoryData = ownerStoreDao.getStoreCategoryByStoreId(storeId);
	        facilityData = ownerStoreDao.getFacilityByStoreId(storeId);
	        storeImageData = ownerStoreDao.getStoreImagesByStoreId(storeId);
	        
	        // 스케줄 정보를 변환할 리스트
	        List<Map<String, Object>> formattedScheduleData = new ArrayList<>();
	        
	        // 각 스케줄 데이터를 변환
	        for (ScheduleVO schedule : scheduleData) {
	            Map<String, Object> formattedSchedule = new HashMap<>();
	            formattedSchedule.put("scheduleId", schedule.getScheduleId());
	            formattedSchedule.put("storeId", schedule.getStoreId());
	            formattedSchedule.put("dayCodeId", schedule.getDayCodeId());
	            
	            // dayCodeId에 따라 요일 설정
	            String dayOfWeek = getDayOfWeek(schedule.getDayCodeId());
	            formattedSchedule.put("dayOfWeek", dayOfWeek);

	            // LocalTime에서 시간과 분을 포맷팅하여 문자열로 변환
	            if (schedule.getOpen() != null) {
	                formattedSchedule.put("open", schedule.getOpen().format(DateTimeFormatter.ofPattern("HH:mm")));
	            }
	            if (schedule.getClose() != null) {
	                formattedSchedule.put("close", schedule.getClose().format(DateTimeFormatter.ofPattern("HH:mm")));
	            }

	            formattedSchedule.put("closeDay", schedule.isCloseDay());
	            formattedScheduleData.add(formattedSchedule);
	        }

	        Map<String, Object> storeDetailData = new HashMap<>();
	        
	        storeDetailData.put("store", storeData);
	        storeDetailData.put("schedule", formattedScheduleData);  // 변환된 스케줄 데이터 추가
	        storeDetailData.put("category", storeCategoryData);
	        storeDetailData.put("facility", facilityData);
	        storeDetailData.put("storeImage", storeImageData);
	        
	        // 가게 정보가 없는 경우
	        if (storeData == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 가게가 없습니다.");
	        }
	        
	        // 가게 정보 반환
	        return ResponseEntity.ok(storeDetailData);
	    } catch (Exception e) {
	        log.error("가게 정보 조회 중 오류 발생: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 정보 조회 중 오류가 발생하였습니다.");
	    }
	}

	@OwnerOnly
	@PostMapping("")
	public ResponseEntity<Object> registerStore(HttpSession session, @RequestPart("storeDTO") StoreDTO store,
			@RequestPart("scheduleDTO") List<ScheduleDTO> schedules,
			@RequestPart("storeCategoryDTO") List<StoreCategoryDTO> category,
			@RequestPart("facilityDTO") List<FacilityDTO> facility,
			@RequestPart(value = "uploadedFiles", required = false) List<MultipartFile> files) {

		// 세션에서 ownerId 가져오기
		
		int ownerId = getOwnerIdFromSession(session).getOwnerId();

		// StoreDTO에 ownerId 설정
		store.setOwnerId(ownerId);

		// 요청 테스트
		requestTest(store, schedules, category, facility, files);
		
		// 가게 스케쥴 변경시
		

		// 가게 저장
		try {
			if (ownerStoreService.createStore(store, schedules, category, facility, files) == 1) {
				return ResponseEntity.ok("가게가 성공적으로 등록되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록에 실패하였습니다.");
			}
		} catch (Exception e) {
			log.error("가게 등록 중 오류 발생: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록 중 오류가 발생하였습니다.");
		}

	}
	
	@OwnerOnly
    @PutMapping("/{storeId}")
    public ResponseEntity<Object> updateStore(
            HttpSession session,
            @PathVariable int storeId,
            @Valid @RequestPart("storeDTO") StoreDTO store,
            @RequestPart(value = "scheduleDTO", required = false) List<ScheduleDTO> schedules,
            @RequestPart(value = "storeCategoryDTO", required = false) List<StoreCategoryDTO> category,
            @RequestPart(value = "facilityDTO", required = false) List<FacilityDTO> facility,
            @RequestPart(value = "uploadedFiles", required = false) List<MultipartFile> updateFiles,
            @RequestPart(value = "deletedImageUrls", required = false) List<Object> deleteImages) throws Exception {
    	
    	// 삭제 요청받은 fileName을 저장하는 리스트
        List<String> deleteImage = new ArrayList<>();
        
        // deleteImages가 null이 아니고 비어있지 않은지 확인
        if (deleteImages != null && !deleteImages.isEmpty()) {
            for (Object obj : deleteImages) {
                if (obj instanceof String) {
                    deleteImage.add((String) obj); // Object를 String으로 캐스팅하여 추가
                }
            }
        }
	    	log.info("Deleted images: {}", deleteImage);
    	
        // 현재 세션에서 ownerId 가져오기
        if (getOwnerIdFromSession(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        int ownerId = getOwnerIdFromSession(session).getOwnerId();
        // 수정할 가게 정보 확인
        StoreVO existingStore = ownerStoreService.getStoreById(storeId);
        if (existingStore == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가게를 찾을 수 없습니다.");
        }

        // 요청한 사용자가 가게의 주인인지 확인
        if (existingStore.getOwnerId() != ownerId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("가게 수정 권한이 없습니다.");
        }

        // StoreDTO에 storeId 설정
        
        store.setStoreId(storeId);
        
        // 요청 테스트 (로그 확인용)
        log.info("PUT 테스트 : " + facility.toString());

        // 가게 수정
        try {
            int result = ownerStoreService.updateStore(storeId, store, schedules, category, facility, updateFiles, deleteImage);
            
            if (result == 1) {
                return ResponseEntity.ok("가게가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 수정에 실패하였습니다.");
            }
        } catch (Exception e) {
            log.error("가게 수정 중 오류 발생: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 수정 중 오류가 발생하였습니다.");
        }
    }

	private StoreDTO getOwnerIdFromSession(HttpSession session) {
		
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		
		return storeSession;
	}

	// 요청 테스트
	private void requestTest(StoreDTO store, List<ScheduleDTO> schedules, List<StoreCategoryDTO> category,
			List<FacilityDTO> facility, List<MultipartFile> files) {
		log.info("store 테스트 : " + store.toString());
		log.info("schedule 테스트 : " + schedules.toString());
		log.info("category 테스트 : " + category.toString());
		log.info("facility 테스트 :" + facility.toString());

		if (files != null) {
			for (MultipartFile file : files) {
				log.info("file 테스트 : {}", file.getOriginalFilename());
			}
		}
	}
	
	// dayCodeId에 따라 요일 반환하는 메서드
	private String getDayOfWeek(int dayCodeId) {
	    switch (dayCodeId) {
	        case 1: return "월요일";
	        case 2: return "화요일";
	        case 3: return "수요일";
	        case 4: return "목요일";
	        case 5: return "금요일";
	        case 6: return "토요일";
	        case 0: return "일요일";
			default:
				return "알 수 없는 요일";
			}
	}
}