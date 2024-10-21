package com.team.goott.admin.users.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.admin.users.persistence.AdminUsersDAO;
import com.team.goott.infra.RegisterValidator;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.infra.UserNotFoundException;
import com.team.goott.infra.UserNotMatchException;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.register.domain.UserRegisterDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminUsersServiceImpl implements AdminUsersService {
	
	@Autowired
	private AdminUsersDAO dao;
	
	@Inject
	private AmazonS3 s3Client;
	
	@Autowired
	private String bucketName;
	
	@Override
	public Map<String, Object> getAllUsers() {
		List<UserDTO> userLists = dao.getAllUsers();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("userCount", userLists.size());
		returnMap.put("userLists", userLists);
		return returnMap;
	}

	@Override
	public UserDTO getUserInfo(int userId) {
		return dao.getUserInfoByUserId(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int patchUserInfo(UserRegisterDTO userUpdateData, int userId) throws UserNotFoundException, UserNotMatchException{
		log.info("UPDATE USERINFO :: {}", userUpdateData.toString());
		UserDTO userData = dao.getUserInfoByUserId(userId);
		if(userData == null) {
			throw new UserNotFoundException();
		}
		Map<String, String> updateData = new HashMap<String, String>();
		updateData.put("userId", userData.getUserId()+"");
		if(!userData.getEmail().equals(userUpdateData.getEmail()))
			updateData.put("email", userUpdateData.getEmail());

		if(!userData.getName().equals(userUpdateData.getName()))
			updateData.put("name", userUpdateData.getName());
			
		if(!userData.getMobile().equals(userUpdateData.getMobile()))
			updateData.put("mobile", userUpdateData.getMobile());
		
		if(!userData.getGender().equals(userUpdateData.getGender())) 
			updateData.put("gender", userUpdateData.getGender());

		if(userUpdateData.getImageFile() != null && userUpdateData.getImageFile().getOriginalFilename().length() > 0) {
			String result = new RegisterValidator().validateImageFile(userUpdateData.getImageFile());
			if(!result.equals("success")) {
				throw new ValidationException(result);
			}
			Map<String, String> imageMap = userImageUpdate(userUpdateData.getImageFile());
			deleteBeforeImageObj(userData.getProfileImageName());
			updateData.put("profileImageName", imageMap.get("imageFileName"));
			updateData.put("profileImageUrl", imageMap.get("imageUrl"));
		}else {
			updateData.put("profileImageName", userData.getProfileImageName());
			updateData.put("profileImageUrl", userData.getProfileImageUrl());
		}
		
		return dao.userInfoUpdate(updateData);
	}

	
	public Map<String, String> userImageUpdate(MultipartFile image) {
		Map<String, String> imageMap = null;
		try {
			log.info(image.getOriginalFilename());
			S3ImageManager imageManager = new S3ImageManager(image, s3Client, bucketName);
			imageMap = imageManager.uploadImage();
			log.info("완료 이미지 URL :: {}", imageMap.get("imageFileName"));
			log.info("완료 이미지 파일명 :: {}", imageMap.get("imageUrl"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageMap;
	}
	
	public boolean deleteBeforeImageObj(String fileName) {
		boolean result = false;
		try {
			result = new S3ImageManager(s3Client, bucketName, fileName).deleteImage();
			if(result) log.info("삭제한 파일명 :: {}", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
