package com.team.goott.admin.review.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

public interface AdminReviewDAO {
	public List<ReviewDTO> getAllReviews(Map<String, String> sortBy);
	public List<ReviewImagesDTO> getAllReviewImages(Map<String, String> sortBy);
	public List<ReviewDTO> getAllDeleteReqReviews();
	public List<ReviewImagesDTO> getAllDeleteReqReviewImages();
	public List<ReviewImagesDTO> getTargetReviewImages(int reviewId);
	public int deleteReview(int reviewId);
	public int deleteReviewImages(int reviewId);
}
