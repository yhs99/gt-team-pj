package com.team.goott.admin.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.store.service.AdminStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class AdminStoreController {

	//private final String UNAUTHORIZED_MESSAGE = "권한이 없습니다.";
	
	@Autowired
	private AdminStoreService adminStoreService;
	
	@GetMapping("/searchDetail")
	public ResponseEntity<Object> getStoresInfo(HttpSession session
												, @RequestParam(name = "categoryId", required = false) List<String> categoryId
												, @RequestParam(name = "sidoCodeId", required = false) List<String> sidoCodeId
												, @RequestParam(name = "searchParam", required = false) String searchParam) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<StoresVO> stores = adminStoreService.getStoresInfo(categoryId, sidoCodeId, searchParam);
		resultMap.put("storeLists", stores);
		resultMap.put("storeCount", stores.size());
		return ResponseEntity.ok(resultMap);
	}
	
	
	public boolean checkAdminSession(HttpSession session) {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		if(adminSession == null) {
			return false;
		}
		return true;
	}
}
