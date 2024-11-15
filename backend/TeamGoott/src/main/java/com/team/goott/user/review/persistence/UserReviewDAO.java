package com.team.goott.user.review.persistence;

import java.util.List;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewPageDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

public interface UserReviewDAO {


List<ReviewDTO> getAllReviews(ReviewPageDTO paging , String sort);

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

ReserveDTO getReserveInfo(int reserveId);

int checkIfImageExist(int imageId);

int changeStatusCodeId(int reserveId, int statusCode);

ReviewDTO selectUserByUserId(int userId);

	int checkIfImageExist(int imageId);

	int changeStatusCodeId(int reserveId, int statusCode);
  
	int setNotification(NotificationDTO notification);


}