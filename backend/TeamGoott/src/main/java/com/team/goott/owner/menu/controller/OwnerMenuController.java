package com.team.goott.owner.menu.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.MenuDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.menu.service.OwnerMenuService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/owner")
public class OwnerMenuController {
	
	@Inject
	OwnerMenuService service;
	
	//주메뉴, 사이드 메뉴 출력
	@GetMapping("/menu")
	public ResponseEntity<Object> getMenu(@RequestParam(value="isMain", defaultValue="true") boolean isMain, HttpSession session ){
		log.info("menuAPI 호출");
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		List<MenuDTO> menu = null;
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			menu = service.getAllMenu(isMain, storeId);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(menu);
	}
	
	@PostMapping("/menu")
	public void addMenu(@RequestBody MenuDTO menu, @RequestParam("file") MultipartFile file){
		log.info("menu : ", menu.toString());
	}
	
	@PutMapping("/menu/{menuId}")
	public ResponseEntity<Object> modifyMenu(@RequestParam("menuId") int menuId, HttpSession session){
		
		
		return null;
	}
	
	
	@DeleteMapping("/menu/{menuId}")
	public ResponseEntity<Object> deleteMenu(@RequestParam("menuId") int menuId, HttpSession session){
		int storeId = 3;
		int result = service.deleteMenu(menuId,storeId);
		return null;
	}

}
