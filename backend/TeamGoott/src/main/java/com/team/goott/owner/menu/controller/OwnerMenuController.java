package com.team.goott.owner.menu.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
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
	
	@Autowired
	private AmazonS3 s3Client;
	
	private final String bucketName = "goott-bucket";
	
	//주메뉴, 사이드 메뉴 출력
	@GetMapping("/menu")
	public ResponseEntity<Object> getMenu(HttpSession session ){
		log.info("menuAPI 호출");
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		Map<String, Object> menu = new HashMap<String, Object>();
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			menu = service.getAllMenu(storeId);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(menu);
	}
	
	//다른 식당 메뉴 출력
	@GetMapping("/menu/{storeId}")
	public ResponseEntity<Object> getMenuByStoreId(@PathVariable("storeId") int storeId, HttpSession session){
		Map<String, Object> menu = new HashMap<String, Object>();
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		if(storeSession != null) {
			menu = service.getAllMenu(storeId);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(menu);
	}
	
	// 메뉴 등록
	@PostMapping("/menu")
	public ResponseEntity<Object> addMenu(@RequestPart("menu") MenuDTO menu, @RequestPart("file") MultipartFile file, HttpSession session){
		int result = 0;
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		if(storeSession != null) {
			result = service.uploadMenu(menu, file);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(result == 1 ? "메뉴 추가 완료" : "메뉴 추가 실패");
	}
	
	//메뉴 수정
	@PutMapping("/menu/{menuId}")
	public ResponseEntity<Object> modifyMenu(@PathVariable("menuId") int menuId, @RequestPart("menu") MenuDTO updateMenu, @RequestPart("file")MultipartFile file ,  HttpSession session){
		int result = 0;
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			MenuDTO originMenu = service.getMenu(menuId);
			if(storeId == originMenu.getStoreId()) {
				result = service.updateMenu(menuId, updateMenu, file, originMenu);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 메뉴에 대한 권한이 없습니다");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(result == 1 ? "메뉴 수정 완료" : "메뉴 수정 실패");
	}
	
	
	// 메뉴 삭제
	@DeleteMapping("/menu/{menuId}")
	public ResponseEntity<Object> deleteMenu(@PathVariable("menuId") int menuId, HttpSession session){
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		int result = 0;
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			MenuDTO menu = service.getMenu(menuId);
			log.info(menu.toString());
			if(menu.getStoreId() == storeId) {
				result = service.deleteMenu(menuId,storeId);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 메뉴에 대한 권한이 없습니다");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		
		return ResponseEntity.ok(result == 1 ? "메뉴 삭제 성공" : "메뉴 삭제 실패");
	}

}
