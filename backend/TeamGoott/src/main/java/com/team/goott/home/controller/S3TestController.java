package com.team.goott.home.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.home.service.S3TestService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class S3TestController {
	
	@Autowired
	private S3TestService s3Service;
	
	// 파일 업로드 처리
	@PostMapping("/image")
	public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
		String originalFileName = file.getOriginalFilename(); 
		
		// ========= 파일명 중복 방지 처리 ========= //
		String uuidFileName = getUuidFileName(originalFileName);
		log.info(originalFileName);
		log.info(uuidFileName);
		// ========= 서버에 파일 저장 ========= // 
		String res = s3Service.uploadObject(file, uuidFileName);
		
		return ResponseEntity.ok(res);
	}

	@DeleteMapping("/image")
	public ResponseEntity<Object> deleteFile(@RequestParam("fileName") String fileName) {
		log.info("DELETE :: " + fileName);
		return ResponseEntity.ok(s3Service.deleteObject(fileName));
	}
	
	private static String getUuidFileName(String originalFileName) {
		return UUID.randomUUID().toString() + "_" + originalFileName;
	}  
}
