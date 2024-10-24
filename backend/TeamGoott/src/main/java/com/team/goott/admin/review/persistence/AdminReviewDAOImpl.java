package com.team.goott.admin.review.persistence;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.admin.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

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

}
