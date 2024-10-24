package com.team.goott.admin.review.service;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.ReviewVO;
import com.team.goott.infra.ImageDeleteFailedException;

public interface AdminReviewService {
	public List<ReviewVO> getAllReivews(Map<String, String> sortBy);
	public List<ReviewVO> getAllDeleteRequestedReviews();
	public boolean deleteReview(int reviewId) throws ImageDeleteFailedException, Exception;
}
