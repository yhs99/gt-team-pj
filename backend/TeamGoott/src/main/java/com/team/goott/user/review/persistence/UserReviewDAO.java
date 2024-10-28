package com.team.goott.user.review.persistence;

import java.util.List;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewPageDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

public interface UserReviewDAO {

	List<ReviewDTO> getAllReviews(ReviewPageDTO paging);

	ReviewDTO reviewByNo(int reviewId);

	int insertReview(ReviewDTO reviewDTO);

	int delReview(int reviewId);

	int insertImgs(ReviewImagesDTO reviewImg);

	List<ReviewImagesDTO> filesByNo(int reviewId);

	int delFiles(int reviewId);

	int updateReview(ReviewDTO reviewDTO);

	void deleteImgs(int imageId);

	List<ReviewDTO> getMyReviews(ReviewPageDTO paging);

	List<ReserveDTO> getReserveByUserId(int UserId);

	List<ReviewDTO> getUserReviews(int userId);
}
