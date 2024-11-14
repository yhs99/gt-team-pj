package com.team.goott.user.review.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

public interface UserReviewService {

	List<ReviewDTO> getAllReviews(int storeId, int page, int size);

	ReviewDTO reviewByNo(int reviewId);

	boolean addReviewPics(ReviewDTO reviewDTO);

	ReviewImagesDTO imageIntoDTO(int reviewId, MultipartFile file) throws IOException, Exception;

	boolean deleteReviewNFile(int reviewId, int reserveId, List<ReviewImagesDTO> imgDtoList);

	boolean updateReview(ReviewDTO reviewDTO);

	List<ReviewDTO> getMyReview(int userId, int page, int size);

	List<ReserveDTO> getStatusByUserId(int userId);

	List<ReviewDTO> getUserReview(int userId);

	ReserveDTO getReserveInfoByReserveId(int reservationId);
	
	List<ReviewImagesDTO> selectReviewImagesByReviewId(int reviewId);

	int checkImageExist(int imageId);



}
