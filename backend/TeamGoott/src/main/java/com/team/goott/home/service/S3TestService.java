package com.team.goott.home.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3TestService {	
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Autowired
	private String bucketName;
	
	public String uploadObject(MultipartFile multipartFile, String storedFileName) throws IOException {

		String filePath = "/" + storedFileName;
		// 1. bucket name, 2. key : full path to the file 3. file : new File로 생성한 file instance  
		// 2. PutObjectRequest로 구현 가능 
		s3Client.putObject(new PutObjectRequest(bucketName, filePath, multipartFile.getInputStream(), null));
		
		return s3Client.getUrl(bucketName, filePath).toString();
	}

	public String deleteObject(String storedFileName) {
		try {
			log.info("존재 하는지 : "+s3Client.doesObjectExist(bucketName, "/" + storedFileName));
			// deleteObjct(bucket이름, /삭제할 파일명)
			s3Client.deleteObject(bucketName, "/" + storedFileName);
		}catch (AmazonServiceException ase) {
			ase.printStackTrace();
            return ase.getMessage();
        } catch (AmazonClientException ace) {
        	ace.printStackTrace();
            return ace.getMessage();
        }
		return "deleted";
	}
}
