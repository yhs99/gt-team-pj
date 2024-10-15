package com.team.goott.owner.review.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.review.service.OwnerReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OwnerReviewController {

	
	@Inject
	OwnerReviewService service;
	
	@GetMapping("/review")
	public ResponseEntity<Object> getAllReview() {
		List<ReviewVO> reviewes = service.getAllReview();
		return ResponseEntity.ok(reviewes);
	}
	
	
}
