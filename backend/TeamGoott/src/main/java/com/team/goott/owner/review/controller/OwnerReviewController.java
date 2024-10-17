package com.team.goott.owner.review.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.review.service.OwnerReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OwnerReviewController {

	
	@Inject
	OwnerReviewService service;
	
		
	// 전체 리뷰 목록, 총 리뷰수, 총 평점, 당일 리뷰수, 당일 리뷰 총 평점 가져오기
	@GetMapping("/review")
	public ResponseEntity<Object> getAllReview(@RequestParam(value = "sortMethod", defaultValue = "score") String sortMethod, HttpSession session) {
		StoreDTO storeDTO = (StoreDTO) session.getAttribute("store");
		ReviewInfoVO reviews =  null;
		if(storeDTO != null) {
			reviews = service.getTotalReviewInfo(storeDTO.getStoreId(),sortMethod);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		log.info("review : " + reviews.toString());
		return ResponseEntity.ok(reviews);
	}
	
	// 삭제 요청
	@DeleteMapping("/review/{reviewId}")
	public ResponseEntity<Object> deleteReviewRequest(@PathVariable("reviewId") int reviewId){
		ReviewVO review = service.getReview(reviewId);
		int result = service.deleteReviewReq(reviewId, review.isDeleteReq());
		return ResponseEntity.ok(result);
	}
}
