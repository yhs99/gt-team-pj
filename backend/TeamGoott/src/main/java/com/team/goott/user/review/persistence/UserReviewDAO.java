package com.team.goott.user.review.persistence;

import java.util.List;

import com.team.goott.user.domain.ReviewDTO;

public interface UserReviewDAO {

	List<ReviewDTO> getAllReviews() throws Exception;
}
