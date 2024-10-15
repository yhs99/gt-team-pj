package com.team.goott.owner.review.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.ReviewVO;
import com.team.goott.user.domain.ReviewDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerReviewDAOImpl implements OwnerReviewDAO {
	
	@Inject
	private SqlSession ses;
	
	private static String ns = "com.team.mappers.owner.review.ownerReviewMapper.";
	
	@Override
	public List<ReviewVO> getAllReview() {
		return ses.selectList(ns+"getAllReview");
	}

}
