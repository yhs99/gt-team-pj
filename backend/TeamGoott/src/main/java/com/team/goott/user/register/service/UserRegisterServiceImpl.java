package com.team.goott.user.register.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
		if(user.getImageFile() != null) {
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

	@Override
	public UserDTO userInfo(int userId) {
		// 회원 정보 가져오기
		return dao.userInfo(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void userUpdate(UserDTO userDTO, MultipartFile imageFile, HttpSession session) throws ValidationException, Exception {
		// 회원 정보 수정
		// 이름 , 휴대폰 , 프로필 수정
		Map<String, String> imageInfo = new HashMap<String, String>();
		imageInfo.put("imageUrl", null);
		imageInfo.put("imageFileName", null);
		// 수정 정보 유효성 검사
		String msg = new RegisterValidator().validateName(userDTO.getName());
		if (msg.equals("success")) {
			msg = new RegisterValidator().validatePhoneNumber(userDTO.getMobile());
		}
		if (msg.equals("success")&&imageFile!=null) {
			// 변경할 이미지 파일이 있을경우만 체크
			msg = new RegisterValidator().validateImageFile(imageFile);
		}
		// 유저 회원정보 수정시 유효성검사  ValidationException 발생
		if(!msg.equals("success")) {
			throw new ValidationException(msg);
		}
		
		// 유저 프로필 이미지 업로드
		if(imageFile!=null&&!imageFile.getOriginalFilename().isEmpty()) {
			S3ImageManager imageManager = new S3ImageManager(imageFile, s3Client, bucketName);
			imageInfo = imageManager.uploadImage();
			String ProfileImageName = dao.userInfo(userDTO.getUserId()).getProfileImageName();
			
			if (!ProfileImageName.equals("defaultUser.jpg")) {
				// 기본이미지 아닐경우 기존 서버 이미지 파일 삭제
				imageManager = new S3ImageManager(s3Client, bucketName, ProfileImageName);
				imageManager.deleteImage();
			}
			userDTO.setProfileImageUrl(imageInfo.get("imageUrl"));
			userDTO.setProfileImageName(imageInfo.get("imageFileName"));
		}
		// 업데이트 유저 
		if(dao.userUpdateProcess(userDTO) > 0) {
			session.invalidate();
		}
	}

}
