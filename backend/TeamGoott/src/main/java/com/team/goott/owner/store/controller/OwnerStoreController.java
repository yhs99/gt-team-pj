package com.team.goott.owner.store.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
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