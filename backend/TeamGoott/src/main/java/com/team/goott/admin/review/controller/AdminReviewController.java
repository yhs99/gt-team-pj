package com.team.goott.admin.review.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.ReviewVO;
import com.team.goott.admin.review.service.AdminReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminReviewController {
	
	@Inject
	private AdminReviewService adminReviewService;
	
	@GetMapping("/review")
	public ResponseEntity<Object> getAllReviews(HttpSession session
												, @RequestParam(required = false) Map<String, String> sortBy) {
		List<ReviewVO> reviews = new ArrayList<ReviewVO>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(checkAdminSession(session)) { 
			reviews = adminReviewService.getAllReivews(sortBy);
			returnMap.put("reviewCount", reviews.size());
			returnMap.put("reviewData", reviews);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
		}
		return ResponseEntity.ok(returnMap);
	}
	
	@GetMapping("/deleteReview")
	public ResponseEntity<Object> getAllDeleteRequestedReviews(HttpSession session) {
		List<ReviewVO> reviews = new ArrayList<ReviewVO>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(checkAdminSession(session)) {
			reviews = adminReviewService.getAllDeleteRequestedReviews();
			returnMap.put("reviewCount", reviews.size());
			returnMap.put("reviewData", reviews);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
		}
		
		return ResponseEntity.ok(returnMap);
	}
	
	@DeleteMapping("/deleteReview/{reviewId}")
	public ResponseEntity<Object> deleteReview(HttpSession session,
											  @PathVariable(name = "reviewId") int reviewId) {
		if(checkAdminSession(session)) {
			try {
				adminReviewService.deleteReview(reviewId);
			}catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
		}
		return ResponseEntity.ok("삭제 완료");
	}
	
	public boolean checkAdminSession(HttpSession session) {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		if(adminSession == null) {
			return false;
		}
		return true;
	}
}
