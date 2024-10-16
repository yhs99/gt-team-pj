package com.team.goott.user.review.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.review.persistence.UserReviewDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserReviewServiceImpl implements UserReviewService {
	
	@Inject
	private UserReviewDAO revDAO;
	
	@Override
	public List<ReviewDTO> getAllReviews() throws Exception {
		List<ReviewDTO> lst = revDAO.getAllReviews();
		return lst;
	}

}
