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
	
	//세션 임의 생성해서 테스트
	@PostMapping("/review")
	public ResponseEntity<Object> saveStoreDTO(@RequestBody StoreDTO storeDTO, HttpSession session) {
		if(storeDTO == null) {
			log.error("Received null StoreDTO");
		}
		session.setAttribute("store", storeDTO);
		StoreDTO dto = (StoreDTO) session.getAttribute("store");
		log.info("storeDTO : ", dto.toString());
		return ResponseEntity.ok(dto);
	}
	
	
	// 전체 리뷰 목록 가져오기
	@GetMapping("/review")
	public ResponseEntity<Object> getAllReview(@RequestParam(value = "sortMethod", defaultValue = "score") String sortMethod,@RequestParam(value = "storeId", defaultValue = "0") int storeId, HttpSession session) {
		StoreDTO storeDTO = (StoreDTO) session.getAttribute("store");
		List<ReviewVO> reviews =  null;
		if(storeDTO != null) {
			reviews = service.getAllReview(storeDTO.getStoreId(),sortMethod);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		
		reviews = service.getAllReview(storeId,sortMethod);
		log.info("review : " + reviews.toString());
		return ResponseEntity.ok(reviews);
	}
	
	// 총 리뷰 갯수, 평점, 당일 날짜 리뷰 갯수, 평점 
	@GetMapping("/review/info")
	public ResponseEntity<Object> getTotalReviewCount(HttpSession session){
		StoreDTO storeDTO = (StoreDTO) session.getAttribute("store");
//		log.info("storeDTO : " +storeDTO);
		ReviewInfoVO reviewInfo = null;
		if(storeDTO != null) {
			reviewInfo = service.getTotalReviewInfo(storeDTO.getStoreId());
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		log.info("info : " + reviewInfo);
		return ResponseEntity.ok(reviewInfo);
	}

	// 삭제 요청
	@DeleteMapping("/review/{reviewId}")
	public ResponseEntity<Object> deleteReviewRequest(@PathVariable("reviewId") int reviewId){
		ReviewVO review = service.getReview(reviewId);
		int result = service.deleteReviewReq(reviewId, review.isDeleteReq());
		return ResponseEntity.ok(result);
	}
}
