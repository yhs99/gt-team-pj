package com.team.goott.admin.review.service;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.ReviewVO;

public interface AdminReviewService {
	public List<ReviewVO> getAllReivews(Map<String, String> sortBy);
}
