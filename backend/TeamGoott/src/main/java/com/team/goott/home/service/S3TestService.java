package com.team.goott.home.service;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
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
		String ext = storedFileName.split("\\.")[1];
		ext = ext.toString() == "jpg" ? "jpeg" : "png";
		String filePath = "/" + storedFileName;
		try {
			// 1. bucket name, 2. key : full path to the file 3. file : new File로 생성한 file instance  
			// 2. PutObjectRequest로 구현 가능 
			ObjectMetadata metaData = new ObjectMetadata();
			// html에 img태그의 src로 url을 줄 경우 image/jpeg, image/png 로 설정해줘야 정상적으로 html에서 로드가 가능함
			// 해당 이미지의 S3 메타데이터 설정
			metaData.setContentType("image/"+ext);
			s3Client.putObject(new PutObjectRequest(bucketName, filePath, multipartFile.getInputStream(), metaData));
			// 해당 이미지의 URL을 받아와 return
		}
		catch(AmazonServiceException e) {
			e.printStackTrace();
			return e.getMessage();
		}catch (AmazonClientException e) {
			e.printStackTrace();
			return e.getMessage();
		}
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
