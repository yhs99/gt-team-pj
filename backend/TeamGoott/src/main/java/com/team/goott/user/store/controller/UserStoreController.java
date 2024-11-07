package com.team.goott.user.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.store.service.AdminStoreService;
import com.team.goott.infra.StoreNotFoundException;
import com.team.goott.owner.menu.service.OwnerMenuService;
import com.team.goott.user.domain.StoreDTO;
import com.team.goott.user.store.service.UserStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/stores")
public class UserStoreController {
	
	@Inject
	private UserStoreService userStoreService;
	
	@Inject
	private AdminStoreService adminStoreService;
	
	@Inject
	OwnerMenuService ownerMenuService;
	
	//모든 식당 조회
	@GetMapping
	public ResponseEntity<Object> getUserStore(){
		List<StoreDTO> store = null;
		
		try {
			store = userStoreService.getAllStores();
		} catch (Exception e) {
			log.error("식당 정보를 가져오는 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		}
			return ResponseEntity.ok(store);
		
		}
	//상세 식당 정보 조회
	@GetMapping("/{storeId}")
	public ResponseEntity<Object> getStoreById(@PathVariable int storeId){
		try {
			List<Object> store = userStoreService.getStoreById(storeId);
			if(store != null) {
				return ResponseEntity.ok(store);
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
		List<StoreDTO> stores;
		 try {
		        stores = userStoreService.getStoresByCategoriesAndSidos(categoryCodeIds, sidoCodeIds);
		    } catch (Exception e) {
		        log.error("식당 정보를 가져오는 중 오류 발생: ", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식당 정보를 가져오는 중 오류가 발생했습니다.");
		    }
		 return ResponseEntity.ok(stores);
	}
	
	@GetMapping("/storeFilters")
	public ResponseEntity<Object> getStoreFilters() {
		return ResponseEntity.ok(userStoreService.getStoreFilters());
	}
	
	//식당 상세정보 조회
	@GetMapping("/store/{storeId}")
	public ResponseEntity<Object> getStoreInfo(HttpSession session
											, @PathVariable(required = true) int storeId) {
		try {
			return ResponseEntity.ok(adminStoreService.getStoreInfoForUpdate(storeId));
		}catch(StoreNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매장을 찾을 수 없습니다.");
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
		}
	}
	
	//식당 메뉴 출력
	@GetMapping("/menu/{storeId}")
	public ResponseEntity<Object> getMenuByStoreId(@PathVariable("storeId") int storeId){
		Map<String, Object> menu = new HashMap<String, Object>();
	
			menu = ownerMenuService.getAllMenu(storeId);
	
		return ResponseEntity.ok(menu);
	}
}
