package com.team.goott.user.review.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.reviewImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserReviewDAOImpl implements UserReviewDAO {
	
	@Inject
	private SqlSession ses; 
	
	private static String ns="com.team.mappers.user.review.userReviewMapper.";
	
	@Override
	public List<ReviewDTO> getAllReviews(int storeId){
		// 리뷰들을 불러온다
		
		return ses.selectList(ns+"getAllrevws",storeId);
	}

	@Override
	public ReviewDTO reviewByNo(int reviewId) {
		// 리뷰 조회
		return ses.selectOne(ns+"getBoardByNo",reviewId);
	}

	@Override
	public int insertReview(ReviewDTO reviewDTO) {
		// 리뷰 작성
		return ses.insert(ns+"insertReview", reviewDTO);
	}

	@Override
	public int delReview(int reviewId) {
		// 리뷰 삭제
		return ses.delete(ns+"deleteReview", reviewId);
	}

	@Override
	public int insertImgs(reviewImagesDTO reviewImg) {
		// 이미지 첨부
		return ses.insert(ns+"insertImgs", reviewImg);
	}

	@Override
	public List<reviewImagesDTO> filesByNo(int reviewId) {
		// 상세조회 첨부된 파일 불러오기
		return ses.selectList(ns+"getFileByNo",reviewId);
	}

	@Override
	public int delFiles(int reviewId) {
		// 상세조회 첨부된 파일 삭제하기
		return ses.delete(ns+"",reviewId);
	}

	@Override
	public int updateReview(ReviewDTO reviewDTO) {
		// 리뷰 수정
		return ses.update(ns+"updateReview", reviewDTO);
	}

	@Override
	public void deleteImgs(int imageId) {
		// 수정시 파일 개별 삭제
		ses.delete(ns+"deleteImgsById", imageId);
	}

}
