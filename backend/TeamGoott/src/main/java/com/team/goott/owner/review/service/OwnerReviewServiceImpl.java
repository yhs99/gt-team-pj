package com.team.goott.owner.review.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.review.persistence.OwnerReviewDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerReviewServiceImpl implements OwnerReviewService {
	
	@Inject
	OwnerReviewDAO reviewDAO;
	


	@Override
	public int deleteReviewReq(int reviewId, boolean isDeleteReq) {
		return  reviewDAO.deleteReviewReq(reviewId, isDeleteReq);
	}

	@Override
	public ReviewVO getReview(int reviewId) {
		return reviewDAO.getReview(reviewId);
	}

	@Override
	public ReviewInfoVO getTotalReviewInfo(int storeId,String sortMethod ) {
		int totalReviewCount = reviewDAO.getTotalReviewCount(storeId);
		int todayReview = reviewDAO.getTotalTodayReview(storeId);
		float totalScore = reviewDAO.getTotalScore(storeId);
		float totalTodayScore = reviewDAO.getTotalTodayScore(storeId);
		List<ReviewVO> reviews = reviewDAO.getAllReview(storeId, sortMethod);
		int[] countScore = new int[5];
		int[] countMonthlyReview = new int[6];
		
		for(ReviewVO review : reviews) {
			int score = review.getScore();
			//해당 리뷰의 timestamp
			Timestamp reviewTimestamp = Timestamp.valueOf(review.getCreateAt
					().toString());
			// timestamp -> LocalDateTime으로 변환
			LocalDateTime reviewTime = reviewTimestamp.toLocalDateTime();
			
			//현재날짜
			LocalDateTime today = LocalDateTime.now();
			//현재날짜 기준 6개월 전 날짜
			LocalDateTime sixMonthsAgo = today.minusMonths(6);
			// 6개월 전 날짜가 리뷰날짜 보다 이전 또는 같다면(즉 6개월이 아직 안지났다는 의미)
			if(sixMonthsAgo.isBefore(reviewTime) || sixMonthsAgo.isEqual(reviewTime)) {
				
				//해당 리뷰의 month
				int reviewMonth = reviewTime.getMonthValue();
				//현재 날짜의 month
				int currentMonth = today.getMonthValue();
				
				int index = (reviewMonth - currentMonth + 12) % 12; 
				
				if(index == 6) {
					index -= 1;
				}
				
				
				if(index >= 0) {
					countMonthlyReview[index]++;
				}
			}
			
			
			switch (score) {
			case 5:
				countScore[0]++;
				break;
			case 4:
				countScore[1]++;
				break;
			case 3:
				countScore[2]++;
				break;
			case 2:
				countScore[3]++;
				break;
			case 1:
				countScore[4]++;
				break;
			}
		}
		
		ReviewInfoVO reviewInfo = ReviewInfoVO.builder().totalReview(totalReviewCount).todayReview(todayReview).totalScore(totalScore).todayTotalScore(totalTodayScore).reviews(reviews).countMonthlyReview(countMonthlyReview).countScore(countScore).build();	
		log.info(reviewInfo.toString());
		
		return reviewInfo;
	}

}
