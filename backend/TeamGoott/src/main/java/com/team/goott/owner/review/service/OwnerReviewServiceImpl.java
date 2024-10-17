package com.team.goott.owner.review.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.review.persistence.OwnerReviewDAO;
import com.team.goott.user.domain.ReviewDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerReviewServiceImpl implements OwnerReviewService {
	
	@Inject
	OwnerReviewDAO reviewDAO;
	
	@Override
	public List<ReviewVO> getAllReview(int storeId,String sortMethod) {
		return reviewDAO.getAllReview(storeId,sortMethod);
	}

	@Override
	public int deleteReviewReq(int reviewId, boolean isDeleteReq) {
		return  reviewDAO.deleteReviewReq(reviewId, isDeleteReq);
	}

	@Override
	public ReviewVO getReview(int reviewId) {
		return reviewDAO.getReview(reviewId);
	}

	@Override
	public ReviewInfoVO getTotalReviewInfo(int storeId) {
		int totalReviewCount = reviewDAO.getTotalReviewCount(storeId);
		int todayReview = reviewDAO.getTotalTodayReview(storeId);
		float totalScore = reviewDAO.getTotalScore(storeId);
		float totalTodayScore = reviewDAO.getTotalTodayScore(storeId);
		
		ReviewInfoVO reviewInfo = ReviewInfoVO.builder().totalReview(totalReviewCount).todayReview(todayReview).totalScore(totalScore).todayTotalScore(totalTodayScore).build();	
		log.info(reviewInfo.toString());
		
		return reviewInfo;
	}

}
