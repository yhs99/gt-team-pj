package com.team.goott.admin.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.ReviewDTO;
import com.team.goott.admin.domain.ReviewVO;
import com.team.goott.admin.review.persistence.AdminReviewDAO;
import com.team.goott.user.domain.ReviewImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminReviewServiceImpl implements AdminReviewService {
	
	@Inject
	private AdminReviewDAO dao;
	
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

	
}
