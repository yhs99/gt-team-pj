package com.team.goott.user.review.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.reviewImagesDTO;

public interface UserReviewService {

	List<ReviewDTO> getAllReviews(int storeId);

	ReviewDTO reviewByNo(int reviewId);

	boolean addReview(ReviewDTO reviewDTO);

	reviewImagesDTO imageIntoDTO(int reviewId, MultipartFile file) throws IOException, Exception;

	boolean deleteReviewNFile(int reviewId, List<reviewImagesDTO> imgDtolst);

	boolean updateReview(ReviewDTO reviewDTO);


}
