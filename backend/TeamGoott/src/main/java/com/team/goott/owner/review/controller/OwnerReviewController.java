package com.team.goott.owner.review.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.review.service.OwnerReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OwnerReviewController {

	
	@Inject
	OwnerReviewService service;
	
	@GetMapping("/review")
	public ResponseEntity<Object> getAllReview(@RequestParam(value = "sortMethod", defaultValue = "score") String sortMethod,@RequestParam(value = "storeId", defaultValue = "0") int storeId, HttpSession session) {
//		session.getAttribute("store");
		List<ReviewVO> reviews =  null;
		reviews = service.getAllReview(storeId,sortMethod);
		log.info("review : " + reviews.toString());
		return ResponseEntity.ok(reviews);
	}
	
	@GetMapping("/review/info")
	public ResponseEntity<Object> getTotalReviewCount(){
		ReviewInfoVO reviewInfo = service.getTotalReviewInfo();
		log.info("info : " + reviewInfo);
		return ResponseEntity.ok(reviewInfo);
	}

	
	@DeleteMapping("/review/{reviewId}")
	public ResponseEntity<Object> deleteReviewRequest(@PathVariable("reviewId") int reviewId){
		ReviewVO review = service.getReview(reviewId);
		int result = service.deleteReviewReq(reviewId, review.isDeleteReq());
		return ResponseEntity.ok(result);
	}
}
