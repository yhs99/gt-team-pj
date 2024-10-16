package com.team.goott.user.review.service;

import java.util.List;

import com.team.goott.user.domain.ReviewDTO;

public interface UserReviewService {

	List<ReviewDTO> getAllReviews() throws Exception;

}
