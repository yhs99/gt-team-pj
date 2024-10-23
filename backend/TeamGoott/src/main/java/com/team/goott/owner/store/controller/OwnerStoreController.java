package com.team.goott.owner.store.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.owner.store.service.OwnerStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/store")
public class OwnerStoreController {

	@Autowired
	private OwnerStoreService ownerStoreService;

	@PostMapping("")
	public ResponseEntity<Object> registerStore(HttpSession session, @RequestPart("storeDTO") StoreDTO store,
			@RequestPart("scheduleDTO") List<ScheduleDTO> schedules,
			@RequestPart("storeCategoryDTO") StoreCategoryDTO category,
			@RequestPart("facilityDTO") FacilityDTO facility,
			@RequestPart(value = "file", required = false) List<MultipartFile> files) {

		// 세션에서 ownerId 가져오기
		Integer ownerId = getOwnerIdFromSession(session);
		
		if (ownerId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		// StoreDTO에 ownerId 설정
		store.setOwnerId(ownerId);

		// 요청 테스트
		requestTest(store, schedules, category, facility, files);

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
	
    @PutMapping("/{storeId}")
    public ResponseEntity<Object> updateStore(
            HttpSession session,
            @PathVariable int storeId,
            @Valid @RequestPart("storeDTO") StoreDTO store,
            @RequestPart(value = "scheduleDTO", required = false) List<ScheduleDTO> schedules,
            @RequestPart(value = "storeCategoryDTO", required = false) StoreCategoryDTO category,
            @RequestPart(value = "facilityDTO", required = false) FacilityDTO facility,
            @RequestPart(value = "file", required = false) List<MultipartFile> updateFiles,
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
        Integer ownerId = 6;
//        if (ownerId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//        }

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
        
//        Integer storeId = 84;
        store.setStoreId(storeId);
        
        // 요청 테스트 (로그 확인용)
        log.info("PUT 테스트 : " + existingStore.toString());

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 수정 중 오류가 발생하였습니다.");
        }
    }

	// 세션에서 ownerId 가져오는 메서드
	private Integer getOwnerIdFromSession(HttpSession session) {
		return (Integer) session.getAttribute("ownerId");
	}

	// 요청 테스트
	private void requestTest(StoreDTO store, List<ScheduleDTO> schedules, StoreCategoryDTO category,
			FacilityDTO facility, List<MultipartFile> files) {
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
}