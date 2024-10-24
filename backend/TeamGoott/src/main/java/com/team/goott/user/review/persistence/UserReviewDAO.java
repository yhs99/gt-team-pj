package com.team.goott.user.review.persistence;

import java.util.List;

import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.reviewImagesDTO;

public interface UserReviewDAO {

	List<ReviewDTO> getAllReviews(int storeId);

	ReviewDTO reviewByNo(int reviewId);

	int insertReview(ReviewDTO reviewDTO);

	int delReview(int reviewId);

	int insertImgs(reviewImagesDTO reviewImg);

	List<reviewImagesDTO> filesByNo(int reviewId);

	int delFiles(int reviewId);

	int updateReview(ReviewDTO reviewDTO);

	void deleteImgs(int imageId);
}
