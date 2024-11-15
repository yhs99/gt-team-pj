package com.team.goott.admin.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.store.service.AdminStoreService;
import com.team.goott.infra.StoreNotFoundException;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class AdminStoreController {

	private final String UNAUTHORIZED_MESSAGE = "권한이 없습니다.";
	
	@Autowired
	private AdminStoreService adminStoreService;
	
	@GetMapping("/searchStores")
	public ResponseEntity<Object> getStoresInfo(HttpSession session
												, @RequestParam(name = "categoryId", required = false) List<String> categoryId
												, @RequestParam(name = "sidoCodeId", required = false) List<String> sidoCodeId
												, @RequestParam(name = "searchParam", required = false) String searchParam
												, @RequestParam(name = "showBlock", required = false) String showBlock) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<StoresVO> stores = adminStoreService.getStoresInfo(categoryId, sidoCodeId, searchParam, showBlock);
		resultMap.put("storeLists", stores);
		resultMap.put("storeCount", stores.size());
		return ResponseEntity.ok(resultMap);
	}
	
	@DeleteMapping("admin/store/{storeId}")
	public ResponseEntity<Object> blockStore(HttpSession session
											, @PathVariable("storeId") int storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.blockStore(storeId) == 1 ? "블럭처리 되었습니다." : "서버 오류로 인해 블럭처리 되지 않았습니다.");
			}catch(StoreNotFoundException e) {
				return ResponseEntity.ok(e.getMessage());
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	@PatchMapping("admin/store/{storeId}")
	public ResponseEntity<Object> blockCancel(HttpSession session
											, @PathVariable("storeId") int storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.cancelBlock(storeId) == 1 ? "블럭해제 처리 되었습니다." : "서버 오류로 인해 블럭해제 처리 되지 않았습니다.");
			}catch(StoreNotFoundException e) {
				return ResponseEntity.ok("해당 블럭 처리된 매장을 찾을 수 없습니다.");
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	public boolean checkAdminSession(HttpSession session) {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		if(adminSession == null) {
			return false;
		}
		return true;
	}
}
