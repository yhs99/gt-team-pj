package com.team.goott.infra;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3ImageManager {

    private AmazonS3 s3Client;
    private String bucketName;
	private ObjectMetadata metaData;
	private MultipartFile multipartImageFile;
	private String fileName;

	/**
	 * @author yhs99
	 * @param s3Client AmazonS3 타입객체
	 * @param bucketName 버킷이름
	 * @param deleteFileName 삭제할 이미지 파일 이름
	 * S3 이미지 객체 삭제를 위한 생성자
	 */
	public S3ImageManager(AmazonS3 s3Client, String bucketName, String deleteFileName) {
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.fileName = deleteFileName;
	}
		
	// 유저 이메일을 받아와 암호화하여 객체생성
	/**
	 * @author yhs99
	 * @param multipartImageFile 업로드 할 이미지 파일
	 * @param s3Client AmazonS3 타입객체
	 * @param bucketName 버킷이름
	 * @throws Exception
	 * S3 이미지 객체 업로드를 위한 생성자입니다
	 */
	public S3ImageManager(MultipartFile multipartImageFile, AmazonS3 s3Client, String bucketName) throws Exception {
		this.multipartImageFile = multipartImageFile;
		this.fileName = "/"+getUuidFileName(multipartImageFile.getOriginalFilename());
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.metaData = new ObjectMetadata();
		this.metaData.setContentType(multipartImageFile.getContentType());
		this.metaData.setContentLength(multipartImageFile.getSize());
	}
	
	// 이미지업로드
	/**
	 * 
	 * @return Map 형식으로 
	 * imageUrl : imageUrl경로
	 * imageFileName : image 저장된 파일명을 반환합니다.
	 * @throws Exception
	 * @throws IOException
	 */
	public Map<String, String> uploadImage() throws Exception, IOException {
		if(this.multipartImageFile != null) {
			try {
				log.info(fileName + " ::: " + bucketName);
				s3Client.putObject(new PutObjectRequest(bucketName, fileName, this.multipartImageFile.getInputStream() , this.metaData));
				Map<String, String> returnObj = new HashMap<String, String>();
				returnObj.put("imageUrl", URLDecoder.decode(s3Client.getUrl(bucketName+"/", fileName.replaceAll("/", ""))+"", "UTF-8"));
				returnObj.put("imageFileName", this.fileName);
				return returnObj;
			} catch(SdkClientException | IOException e) {
				e.printStackTrace();
				throw new ImageUploadFailedException();
			}
		} else {
			throw new Exception("파일 객체를 생성해야합니다.");
		}
	}
	
	public boolean deleteImage() throws Exception {
		try {
			s3Client.deleteObject(bucketName, this.fileName);
			log.info("{} 객체 삭제 완료.", this.fileName);
		} catch(SdkClientException ase) {
			ase.printStackTrace();
			throw new ImageUploadFailedException();
		}
		return true;
	}
    
    public String getUuidFileName(String fileName) {
		return UUID.randomUUID().toString()+"_"+fileName;
    }
}
