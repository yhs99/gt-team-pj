package com.team.goott.admin.review.persistence;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.admin.domain.ReviewDTO;
import com.team.goott.admin.domain.ReviewImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AdminReviewDAOImpl implements AdminReviewDAO {
	
	private final static String NS = "com.team.mappers.admin.review.adminReviewMapper.";
	
	@Inject
	private SqlSession ses;

	@Override
	public List<ReviewDTO> getAllReviews(Map<String, String> sortBy) {
		return ses.selectList(NS+"getAllReviews", sortBy);
	}

	@Override
	public List<ReviewImagesDTO> getAllReviewImages(Map<String, String> sortBy) {
		return ses.selectList(NS+"getAllReviewImages", sortBy);
	}

	@Override
	public List<ReviewDTO> getAllDeleteReqReviews() {
		return ses.selectList(NS+"getAllDeleteReqReviews");
	}

	@Override
	public List<ReviewImagesDTO> getAllDeleteReqReviewImages() {
		return ses.selectList(NS+"getAllDeleteReqReviewImages");
	}

	@Override
	public List<ReviewImagesDTO> getTargetReviewImages(int reviewId) {
		return ses.selectList(NS+"getTargetReviewImages", reviewId);
	}

	@Override
	public int deleteReview(int reviewId) {
		return ses.delete(NS+"deleteReview", reviewId);
	}

	@Override
	public int deleteReviewImages(int reviewId) {
		return ses.delete(NS+"deleteReviewImages", reviewId);
	}

	@Override
	public ReviewDTO getTargetReviewInfo(int reviewId) {
		return ses.selectOne(NS+"getTargetReviewInfo", reviewId);
	}

	@Override
	public int cancelDeleteReview(int reviewId) {
		return ses.update(NS+"cancelDeleteReqReview", reviewId);
	}

}
