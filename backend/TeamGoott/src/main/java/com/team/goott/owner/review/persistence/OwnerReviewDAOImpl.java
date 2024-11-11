package com.team.goott.owner.review.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.ReviewByDateVO;
import com.team.goott.owner.domain.ReviewVO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerReviewDAOImpl implements OwnerReviewDAO {
	
	@Inject
	private SqlSession ses;
	
	private static String ns = "com.team.mappers.owner.review.ownerReviewMapper.";
	
	@Override
	public List<ReviewVO> getAllReview(int storeId, String sortMethod) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("storeId", storeId);
		args.put("sortMethod", sortMethod);
		return ses.selectList(ns+"getAllReview", args);
	}

	@Override
	public int deleteReviewReq(int reviewId, boolean isDeleteReq) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("reviewId", reviewId);
		args.put("isDeleteReq", isDeleteReq);
		return ses.update(ns+"updateisDeleteReq", args);
	}

	@Override
	public ReviewVO getReview(int reviewId) {
		return ses.selectOne(ns+"getReview", reviewId);
	}

	@Override
	public int getTotalReviewCount(int storeId) {
		return ses.selectOne(ns+"getTotalReviewCount", storeId) != null ? ses.selectOne(ns+"getTotalReviewCount", storeId) : 0;
	}

	@Override
	public int getTotalTodayReview(int storeId) {
		return ses.selectOne(ns+"getTotalTodayReview", storeId) != null ? ses.selectOne(ns+"getTotalTodayReview", storeId) : 0 ;
	}

	@Override
	public float getTotalScore(int storeId) {
		return ses.selectOne(ns+"getTotalScore", storeId) != null ? ses.selectOne(ns+"getTotalScore", storeId) : 0 ;
	}

	@Override
	public float getTotalTodayScore(int storeId) {
		return ses.selectOne(ns+"getTotalTodayScore", storeId) != null ? ses.selectOne(ns+"getTotalTodayScore", storeId) : 0;
	}

	@Override
	public List<ReviewByDateVO> reviewByDate(int storeId) {
		return ses.selectList(ns+"getReviewByDate", storeId);
	}

}
