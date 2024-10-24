package com.team.goott.admin.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.admin.domain.ReviewDTO;
import com.team.goott.admin.domain.ReviewVO;
import com.team.goott.admin.review.persistence.AdminReviewDAO;
import com.team.goott.infra.ImageDeleteFailedException;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.user.domain.ReviewImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminReviewServiceImpl implements AdminReviewService {
	
	@Inject
	private AdminReviewDAO dao;
    
	@Inject
	private AmazonS3 s3Client;
	
	@Autowired
	private String bucketName;
	
	@Override
	public List<ReviewVO> getAllReivews(Map<String, String> sortBy) {
		List<ReviewDTO> reviews = dao.getAllReviews(sortBy);
		log.info("{}", sortBy.toString());
		List<ReviewImagesDTO> reviewImages = dao.getAllReviewImages(sortBy);
		List<ReviewVO> vo = new ArrayList<ReviewVO>();
		
		for(ReviewDTO review : reviews) {
			ReviewVO reviewVo = new ReviewVO();
			List<ReviewImagesDTO> imageLists = new ArrayList<ReviewImagesDTO>();
			reviewVo.setReviewId(review.getReviewId());
			reviewVo.setContent(review.getContent());
			reviewVo.setDeleteReq(review.isDeleteReq());
			reviewVo.setCreateAt(review.getCreateAt());
			reviewVo.setDateTime(review.getDateTime());
			reviewVo.setScore(review.getScore());
			reviewVo.setUserId(review.getUserId());
			reviewVo.setName(review.getName());
			reviewVo.setProfileImageUrl(review.getProfileImageUrl());
			reviewVo.setStoreId(review.getStoreId());
			for(ReviewImagesDTO image : reviewImages) {
					if(image.getReviewId() == review.getReviewId()) {
						imageLists.add(image);
					}
			}
			reviewVo.setReviewImages(imageLists);
			reviewVo.setReviewImagesCount(imageLists.size());
			vo.add(reviewVo);
		}
		return vo;
	}

	@Override
	public List<ReviewVO> getAllDeleteRequestedReviews() {
		List<ReviewDTO> reviews = dao.getAllDeleteReqReviews();
		List<ReviewImagesDTO> reviewImages = dao.getAllDeleteReqReviewImages();
		List<ReviewVO> vo = new ArrayList<ReviewVO>();
		
		for(ReviewDTO review : reviews) {
			ReviewVO reviewVo = new ReviewVO();
			List<ReviewImagesDTO> imageLists = new ArrayList<ReviewImagesDTO>();
			reviewVo.setReviewId(review.getReviewId());
			reviewVo.setContent(review.getContent());
			reviewVo.setDeleteReq(review.isDeleteReq());
			reviewVo.setCreateAt(review.getCreateAt());
			reviewVo.setDateTime(review.getDateTime());
			reviewVo.setScore(review.getScore());
			reviewVo.setUserId(review.getUserId());
			reviewVo.setName(review.getName());
			reviewVo.setProfileImageUrl(review.getProfileImageUrl());
			reviewVo.setStoreId(review.getStoreId());
			for(ReviewImagesDTO image : reviewImages) {
					if(image.getReviewId() == review.getReviewId()) {
						imageLists.add(image);
					}
			}
			reviewVo.setReviewImages(imageLists);
			reviewVo.setReviewImagesCount(imageLists.size());
			vo.add(reviewVo);
		}
		return vo;
	}

	@Override
	@Transactional(rollbackFor = {ImageDeleteFailedException.class, Exception.class})
	public boolean deleteReview(int reviewId) throws ImageDeleteFailedException, Exception {
		ReviewDTO targetReviewInfo = dao.getTargetReviewInfo(reviewId);
		if(targetReviewInfo == null ) {
			throw new Exception("유효하지 않은 리뷰이거나, 삭제 요청이 되지 않은 리뷰입니다.");
		}
		List<ReviewImagesDTO> targetReviewImageInfo = dao.getTargetReviewImages(reviewId);
		if(targetReviewImageInfo != null && targetReviewImageInfo.size() > 0) {
			dao.deleteReviewImages(reviewId);
			for (ReviewImagesDTO reviewImagesDTO : targetReviewImageInfo) {
				String fileName = reviewImagesDTO.getFileName();
				try {
					boolean result = new S3ImageManager(s3Client, bucketName, fileName)
										.deleteImage();
					if(!result) throw new ImageDeleteFailedException("이미지 객체 삭제 실패", fileName);
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			}
		}
		
		return dao.deleteReview(reviewId) == 1 ? true : false;
	}

	
}
