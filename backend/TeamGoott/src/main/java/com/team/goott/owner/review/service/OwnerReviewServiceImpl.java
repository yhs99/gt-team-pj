package com.team.goott.owner.review.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.review.persistence.OwnerReviewDAO;
import com.team.goott.user.domain.ReviewDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerReviewServiceImpl implements OwnerReviewService {
	
	@Inject
	OwnerReviewDAO reviewDAO;
	
	@Override
	public List<ReviewVO> getAllReview() {
		return reviewDAO.getAllReview();
	}

}
