package com.team.goott.owner.review.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.ReviewInfoVO;
import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.review.service.OwnerReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/owner")
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
		return ResponseEntity.ok(reviews);
	}
	
	// 삭제 요청
	@DeleteMapping("/review/{reviewId}")
	public ResponseEntity<Object> deleteReviewRequest(HttpSession session, @PathVariable("reviewId") int reviewId){
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		int result;
		if(storeSession != null) {
			ReviewVO review = service.getReview(reviewId);
			if(review.getStoreId() == storeSession.getStoreId()) {
				result = service.deleteReviewReq(reviewId, review.isDeleteReq());
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 리뷰에 대한 권한이 없습니다.");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(result == 1 ? "삭제 요청 성공" : "삭제 실패");
	}
}
