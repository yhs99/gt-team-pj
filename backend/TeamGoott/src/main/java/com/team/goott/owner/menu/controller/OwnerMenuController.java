package com.team.goott.owner.menu.controller;

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
	public ResponseEntity<Object> addMenu(@RequestPart("menu") MenuDTO menu, @RequestPart("file") MultipartFile file, HttpSession session){
		MenuDTO uploadMenu = null;
		int result = 0;
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		if(storeSession != null) {
			try {
				uploadMenu = menu;
				S3ImageManager s3ImageManager = new S3ImageManager(file, s3Client, bucketName);
				Map<String, String> uploadImageInfo = s3ImageManager.uploadImage();
				uploadMenu.setMenuImageUrl(uploadImageInfo.get("imageUrl"));
				uploadMenu.setMenuImageName(uploadImageInfo.get("imageFileName"));
				System.out.println("uploadMenu : " + uploadMenu.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = service.uploadMenu(uploadMenu);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		
		return ResponseEntity.ok(result == 1 ? "메뉴 추가 완료" : "메뉴 추가 실패");
	}
	
	@PutMapping("/menu/{menuId}")
	public ResponseEntity<Object> modifyMenu(@PathVariable("menuId") int menuId, @RequestPart("menu") MenuDTO menu, @RequestPart("file")MultipartFile file ,  HttpSession session){
		MenuDTO modifyMenu = null;
		int result = 0;
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		int storeId = storeSession.getStoreId();
		
		if(storeSession != null) {
			MenuDTO getMenu = service.getMenu(menuId);
			if(storeId == getMenu.getStoreId()) {
				try {
					modifyMenu = menu;
					S3ImageManager s3ImageManager = new S3ImageManager(file, s3Client, bucketName);
					Map<String, String> uploadImageInfo = s3ImageManager.uploadImage();
					modifyMenu.setMenuImageUrl(uploadImageInfo.get("imageUrl"));
					modifyMenu.setMenuImageName(uploadImageInfo.get("imageFileName"));
					System.out.println("modifyMenu : " + modifyMenu.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				result = service.updateMenu(menuId, modifyMenu);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 메뉴에 대한 권한이 없습니다");
			}
			
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		
		
		
		return ResponseEntity.ok(result == 1 ? "메뉴 수정 완료" : "메뉴 수정 실패");
	}
	
	
	@DeleteMapping("/menu/{menuId}")
	public ResponseEntity<Object> deleteMenu(@PathVariable("menuId") int menuId, HttpSession session){
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		int storeId = storeSession.getStoreId();
		int result = 0;
		if(storeSession != null) {
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
