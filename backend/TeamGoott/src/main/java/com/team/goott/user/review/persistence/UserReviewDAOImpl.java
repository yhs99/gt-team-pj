package com.team.goott.user.review.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.ReviewDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserReviewDAOImpl implements UserReviewDAO {
	
	@Inject
	private SqlSession ses; 
	
	private static String ns="com.team.mappers.user.review.userReviewMapper.";
	
	@Override
	public List<ReviewDTO> getAllReviews() throws Exception {
		// 리뷰들을 불러온다
		
		return ses.selectList(ns+"getAllrevws");
	}

}
