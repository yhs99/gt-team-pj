package com.team.goott.owner.menu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.owner.domain.MenuDTO;
import com.team.goott.owner.menu.persistence.OwnerMenuDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerMenuServiceImpl implements OwnerMenuService {
	
	@Inject
	OwnerMenuDAO menuDAO;
	
	@Autowired
	private AmazonS3 s3Client;
	
	private final String bucketName = "goott-bucket";
	
	@Override
	public Map<String, Object> getAllMenu(int storeId) {
		Map<String, Object> allMenuInfo = new HashMap<String, Object>();
		List<MenuDTO> dishes = menuDAO.getAllMenu(storeId);
		int numOfMain = 0;
		int numOfSide = 0;
		for(MenuDTO dish : dishes) {
			if(dish.isMain()) {
				allMenuInfo.put("main", dish);
				numOfMain++;
			}else {
				allMenuInfo.put("side", dish);
				numOfSide++;
			}
		}
		allMenuInfo.put("numOfAllMenu", dishes.size());
		allMenuInfo.put("numOfMainMenu", numOfMain);
		allMenuInfo.put("numOfSideMenu", numOfSide);
		
		log.info("{}", allMenuInfo.toString());
		
		return allMenuInfo;
	}

	@Override
	public int deleteMenu(int menuId,int storeId) {
		return menuDAO.deleteMenu(menuId, storeId);
	}

	@Override
	public MenuDTO getMenu(int menuId) {
		return menuDAO.getMenu(menuId);
	}

	@Override
	public int uploadMenu(MenuDTO menu, MultipartFile file, int storeId) {
		MenuDTO uploadMenu = menu;
		log.info("{}",file.getOriginalFilename().isEmpty());
		try {
			//이미지 파일 처리
			if(!file.getOriginalFilename().isEmpty()) {
				S3ImageManager s3ImageManager = new S3ImageManager(file, s3Client, bucketName);
				Map<String, String> uploadImageInfo = s3ImageManager.uploadImage();
				uploadMenu.setMenuImageUrl(uploadImageInfo.get("imageUrl"));
				uploadMenu.setMenuImageName(uploadImageInfo.get("imageFileName"));
			} else {
				uploadMenu.setMenuImageName("noImage.jpg");
				uploadMenu.setMenuImageUrl("https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg");
			}
			uploadMenu.setStoreId(storeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return menuDAO.uploadMenu(uploadMenu);
	}

	@Override
	public int updateMenu(int menuId, MenuDTO updateMenu, MultipartFile file, MenuDTO originMenu) {
		MenuDTO modifyMenu = updateMenu;
		try {
			// 수정 메뉴 이미지 파일 처리
			if(!file.getOriginalFilename().isEmpty()) {
				S3ImageManager uploadS3ImageManager = new S3ImageManager(file, s3Client, bucketName);
				S3ImageManager deleteS3ImageManager = new S3ImageManager(s3Client, bucketName, originMenu.getMenuImageName());
				
				if(deleteS3ImageManager.deleteImage()) {
					log.info("기존 이미지 삭제 완료");
					Map<String, String> uploadImageInfo = uploadS3ImageManager.uploadImage();
					modifyMenu.setMenuImageUrl(uploadImageInfo.get("imageUrl"));
					modifyMenu.setMenuImageName(uploadImageInfo.get("imageFileName"));
				} else {
					log.info("이미지 삭제 실패");
					return 0;
				}
			} else {
				modifyMenu.setMenuImageName("noImage.jpg");
				modifyMenu.setMenuImageUrl("https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuDAO.updateMenu(menuId, modifyMenu);
	}

}
