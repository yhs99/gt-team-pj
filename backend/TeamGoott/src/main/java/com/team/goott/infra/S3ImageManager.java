package com.team.goott.infra;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3ImageManager {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1234567890123456";
    
    private AmazonS3 s3Client;
    private String bucketName;
	private ObjectMetadata metaData;
	private MultipartFile multipartImageFile;
	private String fileName;
	
	// 유저 이메일을 받아와 암호화하여 객체생성
	public S3ImageManager(String userEmail, AmazonS3 s3Client, String bucketName) throws Exception {
		this.fileName = "/"+encrypt(userEmail);
		this.s3Client = s3Client;
		this.bucketName = bucketName;
	}
		
	// 유저 이메일을 받아와 암호화하여 객체생성
	public S3ImageManager(MultipartFile multipartImageFile, String userEmail, AmazonS3 s3Client, String bucketName) throws Exception {
		this.multipartImageFile = multipartImageFile;
		String fileExt = "."+multipartImageFile.getOriginalFilename().split("\\.")[1];
		this.fileName = "/"+encrypt(userEmail)+fileExt;
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.metaData = new ObjectMetadata();
		this.metaData.setContentType(multipartImageFile.getContentType());
	}
	
	// 이미지업로드
	public Map<String, String> uploadImage() throws Exception, IOException {
		if(this.multipartImageFile != null) {
			try {
				s3Client.putObject(new PutObjectRequest(bucketName, fileName, this.multipartImageFile.getInputStream() , this.metaData));
				Map<String, String> returnObj = new HashMap<String, String>();
				returnObj.put("profileImageUrl", s3Client.getUrl(bucketName, fileName)+"");
				returnObj.put("profileImageName", this.fileName);
				return returnObj;
			} catch(AmazonServiceException ase) {
				ase.printStackTrace();
				throw new Exception(ase.getMessage());
			} catch(AmazonClientException ase) {
				ase.printStackTrace();
				throw new Exception(ase.getMessage());
			}
		} else {
			throw new Exception("파일 객체를 생성해야합니다.");
		}
	}
	
	public boolean deleteImage() throws Exception {
		try {
			s3Client.deleteObject(bucketName, this.fileName);
		} catch(AmazonServiceException ase) {
			ase.printStackTrace();
			throw new Exception(ase.getMessage());
		} catch(AmazonClientException ase) {
			ase.printStackTrace();
			throw new Exception(ase.getMessage());
		}
		return true;
	}
	
    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
