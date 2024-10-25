package com.team.goott.user.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.store.service.UserStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stores")
public class UserStoreController {
	
	@Inject
	private UserStoreService userStoreService;
	
	//모든 식당 조회
	@GetMapping
	public ResponseEntity<Object> getAllStore(){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = userStoreService.getAllStores();
		} catch (Exception e) {
			log.error("식당 정보를 가져오는 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		}
			return ResponseEntity.ok(result);
		
		}
	//상세 식당 정보 조회
	@GetMapping("/{storeId}")
	public ResponseEntity<Object> getStoreById(@PathVariable int storeId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Object> store = userStoreService.getStoreById(storeId);
			result = userStoreService.getDetailsStoreById(storeId);
			if(store != null) {
				return ResponseEntity.ok(result);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("식당 정보를 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		}
	}
		
	//필터링으로 식당 조회
	@GetMapping("/filter")
	public ResponseEntity<Object> getFilteredStores(
			@RequestParam(required = false) List<String> categoryCodeIds, 
	        @RequestParam(required = false) List<Integer> sidoCodeIds){
		Map<String, Object> result;
		 try {
			 result = userStoreService.getStoresByCategoriesAndSidos(categoryCodeIds, sidoCodeIds);
		    } catch (Exception e) {
		        log.error("식당 정보를 가져오는 중 오류 발생: ", e);
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		    }
		 return ResponseEntity.ok(result);
	}
}
