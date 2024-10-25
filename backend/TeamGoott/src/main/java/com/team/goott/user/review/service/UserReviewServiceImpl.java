package com.team.goott.user.review.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.reviewImagesDTO;
import com.team.goott.user.domain.reviewImagesStatus;
import com.team.goott.user.review.persistence.UserReviewDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserReviewServiceImpl implements UserReviewService {
	
	@Autowired
	private AmazonS3 s3Client;
	
	private final String bucketName = "goott-bucket";
	
	@Inject
	private UserReviewDAO revDAO;
	

	
	@Override
	public List<ReviewDTO> getAllReviews(int storeId) {
		List<ReviewDTO> lst = revDAO.getAllReviews(storeId);
		for(ReviewDTO list : lst) {
			log.info("{}",list.getCreateAtLocalDateTime().toString());
		}
		return lst;
	}

	@Override
	public ReviewDTO reviewByNo(int reviewId) {
		// 리뷰 조회
		ReviewDTO review = revDAO.reviewByNo(reviewId);
		List<reviewImagesDTO> imagesDTO = new ArrayList<reviewImagesDTO>();
		imagesDTO = revDAO.filesByNo(reviewId);
		review.setReviewImages(imagesDTO);
		
		return review;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	public boolean addReview(ReviewDTO reviewDTO) {
		// 리뷰 추가
		boolean result=false;
		if(revDAO.insertReview(reviewDTO)==1) {
			int generatedId = reviewDTO.getReviewId();
			if(reviewDTO.getReviewImages() != null) {
				for(reviewImagesDTO imgs : reviewDTO.getReviewImages()) {
					imgs.setReviewId(generatedId);
					revDAO.insertImgs(imgs);
				}
			}
			
			log.info("리뷰 저장 완료");
			result = true;
		}
		return result;
	}




	@Override
	public reviewImagesDTO imageIntoDTO(int reviewId, MultipartFile file) throws IOException, Exception {
		reviewImagesDTO reviewImg = new reviewImagesDTO();
		
		System.out.println("리뷰아이디:"+reviewId);
		S3ImageManager imageManager = new S3ImageManager(file, s3Client, bucketName);
		Map<String, String> reviewImages = imageManager.uploadImage();
		String originalFileName=file.getOriginalFilename();
		String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
		reviewImg.setUrl(reviewImages.get("imageUrl"));
		reviewImg.setFileName(reviewImages.get("imageFileName"));
		reviewImg.setFileType(ext);
		reviewImg.setReviewId(reviewId);
		
		log.info("reivewImage DTO : " + reviewImg.toString());
		
		return reviewImg;
	}

	@Override
	@Transactional
	public boolean deleteReviewNFile(int reviewId, List<reviewImagesDTO> imgDtolst) {
		// 리뷰 삭제, 파일 삭제
		boolean result = false;
		
		try {
		if(revDAO.delReview(reviewId)==1) {
			if(revDAO.delFiles(reviewId)==1) {
			
				for(reviewImagesDTO fileName : imgDtolst) {
					result = new S3ImageManager(s3Client, bucketName, fileName.getFileName()).deleteImage();
					if(result) log.info("파일 삭제 완료 : "+ fileName.getFileName());
				}
			}
		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	
		return result;
	}


	@Override
	@Transactional
	public boolean updateReview(ReviewDTO modifyReview) {
		// 리뷰 수정
		boolean result = false;
		if(revDAO.updateReview(modifyReview) == 1) {
			for(reviewImagesDTO img : modifyReview.getReviewImages()) {
				if (img.getFileStatus() == reviewImagesStatus.INSERT) {
					img.setReviewId(modifyReview.getReviewId());
					revDAO.insertImgs(img);
				}else if(img.getFileStatus() == reviewImagesStatus.DELETE) {
					revDAO.deleteImgs(img.getImageId());
				}
			}
			result = true;
		}
		
		return result;
	}
	

}
