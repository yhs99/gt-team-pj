package com.team.goott.owner.review.persistence;

import java.util.List;

import com.team.goott.owner.domain.ReviewVO;

public interface OwnerReviewDAO {
	//	정렬방식에 따라 전체 리뷰를 불러온다
	List<ReviewVO> getAllReview(int storeId, String sortMethod);

	//리뷰 삭제 요청
	int deleteReviewReq(int reviewId, boolean isDeleteReq);

	//한개의 리뷰 정보 가져오기
	ReviewVO getReview(int reviewId);

	//총 리뷰 수 
	int getTotalReviewCount(int storeId);

	// 총 오늘의 리뷰 수
	int getTotalTodayReview(int storeId);

	// 총 리뷰 평점
	float getTotalScore(int storeId);

	// 총 오늘의 리뷰 평점
	float getTotalTodayScore(int storeId);
	
}
