package com.team.goott.user.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.infra.ApiResponse;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.review.service.UserReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserReviewController {
	
	@Autowired
	private UserReviewService service;
	
	 @RequestMapping(value = "/review", method = RequestMethod.GET)
	    public ResponseEntity<Object> getAllReview(){
	      try {
			ApiResponse<List<ReviewDTO>> lst = (ApiResponse<List<ReviewDTO>>) service.getAllReviews();
			List<ReviewDTO> reviews = lst.getData(); 
			System.out.println(reviews);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	    }
	
	 

}
