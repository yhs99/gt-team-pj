package com.team.goott.owner.review.service;

import java.util.List;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;

public interface OwnerReviewService {
	int deleteReviewReq(int reviewId, boolean isDeleteReq);

	ReviewVO getReview(int reviewId);

	ReviewInfoVO getTotalReviewInfo(int storeId, String sortMethod) ;
}
