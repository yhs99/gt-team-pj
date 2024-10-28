package com.team.goott.user.review.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewPageDTO;
import com.team.goott.user.domain.reviewImagesDTO;
import com.team.goott.user.domain.reviewImagesStatus;
import com.team.goott.user.review.persistence.UserReviewDAO;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserReviewServiceImpl implements UserReviewService {
	
	private static final String INVALID_FILENAME = "Invalid filename";
	
	@Autowired
	private AmazonS3 s3Client;
	
	private final String bucketName = "goott-bucket";
	
	@Inject
	private UserReviewDAO revDAO;
	
	
	@Override
	public List<ReviewDTO> getAllReviews(int storeId, int page, int size) {
		int offset = page * size;
		ReviewPageDTO paging = ReviewPageDTO.builder().storeId(storeId).offset(offset).size(size).build();
	
		List<ReviewDTO> lst = revDAO.getAllReviews(paging);
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
	@Transactional
	public boolean addReviewPics(ReviewDTO reviewDTO) {
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
		// 원본 이미지 저장
		log.info("리뷰아이디 : {}", reviewId);
		S3ImageManager imageManager = new S3ImageManager(file, s3Client, bucketName);
		Map<String, String> reviewImages = imageManager.uploadImage();
		String ext = "";
		String originalFileName=file.getOriginalFilename();
		
		if (originalFileName == null || !originalFileName.contains(".")) {
		    throw new IllegalArgumentException(INVALID_FILENAME);
		}else {
			ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
		}
		
		reviewImg.setUrl(reviewImages.get("imageUrl"));
		reviewImg.setFileName(reviewImages.get("imageFileName"));
		reviewImg.setFileType(ext);
		reviewImg.setReviewId(reviewId);

		log.info("reivewImage DTO : {} ", reviewImg.toString());
		
		return reviewImg;
	}
	

	@Override
	@Transactional
	public boolean deleteReviewNFile(int reviewId, List<reviewImagesDTO> imgDtoList) {
		// 리뷰 삭제, 파일 삭제
		boolean result = false;
		
		try {
			if(revDAO.delReview(reviewId)==1) {
			 log.info("리뷰 삭제 완료");
				
			 if (imgDtoList != null && !imgDtoList.isEmpty()) {
					if(revDAO.delFiles(reviewId)==1) {
					  log.info("파일 삭제 정보 삭제 완료");
	                  log.info("삭제할 이미지 개수: {}", imgDtoList.size());
						
	                  for(reviewImagesDTO fileName : imgDtoList) {
							result = new S3ImageManager(s3Client, bucketName, fileName.getFileName()).deleteImage();
							if(result) {
								log.info("파일 삭제 완료 : "+ fileName.getFileName());
							}
						}
					}
				   }else {
					   result=true;
					   log.info("삭제할 이미지가 없음, 리뷰만 삭제 완료");
				   }
			}
			} catch (Exception e) {
				 log.error("삭제 중 오류 발생: {}", e.getMessage());
			}
			
		return result;
	}


	@Override
	@Transactional
	public boolean updateReview(ReviewDTO modifyReview) {
		// 리뷰 수정
		boolean result = false;
		int reviewId=modifyReview.getReviewId();
		
		if(revDAO.updateReview(modifyReview) == 1) {
			for(reviewImagesDTO img : modifyReview.getReviewImages()) {

				int imageId=img.getImageId();
				
				if (img.getFileStatus() == reviewImagesStatus.INSERT) {
					img.setReviewId(reviewId);
					revDAO.insertImgs(img);
					
					
				}else if(img.getFileStatus() == reviewImagesStatus.DELETE) {
					revDAO.deleteImgs(imageId);
				}
			}
			result = true;
		}
		
		return result;
	}

	@Override
	public List<ReviewDTO> getMyReview(int userId,int page, int size) {
		int offset = page * size;
		ReviewPageDTO paging = ReviewPageDTO.builder().userId(userId).offset(offset).size(size).build();
		List<ReviewDTO> lst = revDAO.getMyReviews(paging);
		for(ReviewDTO list : lst) {
			log.info("{}",list.getCreateAtLocalDateTime().toString());
		}
		
		return lst;
	}

	@Override
	public List<ReserveDTO> getStatusByUserId(int userId) {
		List<ReserveDTO> reserve= revDAO.getReserveByUserId(userId);
		return reserve;
	}

	@Override
	public List<ReviewDTO> getUserReview(int userId) {
		List<ReviewDTO> reviews =revDAO.getUserReviews(userId);
		return reviews;
	}

}