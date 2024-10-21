package com.team.goott.owner.store.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
	public ResponseEntity<Object> registerStore(HttpSession session, @RequestBody ObjectNode saveObj) {
	    // StoreDTO ownerSession = (StoreDTO) session.getAttribute("ownerId");
	    // if (ownerSession == null) {
	    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
	    // }
	    int ownerId = 6; // 세션에서 ownerId를 가져오는 코드로 대체 가능

	    StoreDTO store = null;
	    List<ScheduleDTO> schedules = null;
	    StoreCategoryDTO category = null;
	    FacilityDTO facility = null;

	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());
	    try {
	        store = mapper.treeToValue(saveObj.get("storeDTO"), StoreDTO.class);
	        schedules = mapper.readValue(saveObj.get("scheduleDTO").toString(), new TypeReference<List<ScheduleDTO>>(){}); // List로 변환
	        category = mapper.treeToValue(saveObj.get("storeCategoryDTO"), StoreCategoryDTO.class);
	        facility = mapper.treeToValue(saveObj.get("facilityDTO"), FacilityDTO.class);
	        
	        
	        store.setOwnerId(ownerId);
	        log.info("store 테스트 : " + store.toString());
	        log.info("schedule 테스트 : " + schedules.toString());
	        log.info("category 테스트 : " + category.toString());
	        log.info("facility 테스트 :" + facility.toString());

	        // 가게 저장 로직
	        if (ownerStoreService.createStore(store, schedules, category, facility) == 1) {
	            return ResponseEntity.ok("가게가 성공적으로 등록되었습니다.");
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록에 실패하였습니다.");
	        }
	        
	    } catch (JsonProcessingException | IllegalArgumentException e) {
	        log.error("JSON 변환 또는 인수 오류 발생: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 데이터 형식입니다.");
	    } catch (Exception e) {
	        log.error("가게 등록 중 오류 발생: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록 중 오류가 발생하였습니다.");
	    }
	}
}
