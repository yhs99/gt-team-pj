package com.team.goott.user.register.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.RegisterValidator;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.infra.ValidationException;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.register.domain.UserRegisterDTO;
import com.team.goott.user.register.persistence.UserRegisterDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class UserRegisterServiceImpl implements UserRegisterService {

    @Inject
    private UserRegisterDAO dao;
    
	@Inject
	private AmazonS3 s3Client;
	
	@Autowired
	private String bucketName;
    
	@Override
	public int userRegister(UserRegisterDTO user) throws ValidationException, Exception {
		String msg = new RegisterValidator().batchValidate(user);
		Map<String, String> imageInfo = new HashMap<String, String>();
		imageInfo.put("imageUrl", null);
		imageInfo.put("imageFileName", null);
		
		// 유저 회원가입 유효성검사 일치하지 않을경우 ValidationException 발생
		if(!msg.equals("success")) {
			throw new ValidationException(msg);
		}
		
		// 유저 프로필 이미지 업로드 처리
		if(!user.getImageFile().getOriginalFilename().isEmpty()) {
			S3ImageManager imageManager = new S3ImageManager(user.getImageFile(), s3Client, bucketName);
			imageInfo = imageManager.uploadImage();
		}
		// 유저 데이터 업로드 객체 생성
		return dao.userRegisterProcess(
				UserDTO.builder()
				.email(user.getEmail())
				.password(user.getPassword())
				.name(user.getName())
				.mobile(user.getMobile())
				.gender(user.getGender())
				.profileImageUrl(imageInfo.get("imageUrl"))
				.profileImageName(imageInfo.get("imageFileName"))
				.build());
	}

}
