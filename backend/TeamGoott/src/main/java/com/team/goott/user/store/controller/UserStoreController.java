package com.team.goott.user.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.store.service.UserStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stores")
public class UserStoreController {
	
	private final UserStoreService storeService;
	
	@Autowired
	public UserStoreController(UserStoreService storeService) {
		this.storeService = storeService;
	}
	
	//모든 식당 조회
	@GetMapping
	public ResponseEntity<Object> getUserStore(){
		List<StoreDTO> store = null;
		
		try {
			store = storeService.getAllStores();
		} catch (Exception e) {
			log.error("식당 정보를 가져오는 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		}
			return ResponseEntity.ok(store);
		
		}
	}


