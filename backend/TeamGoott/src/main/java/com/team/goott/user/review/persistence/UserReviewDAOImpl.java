package com.team.goott.user.review.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewPageDTO;
import com.team.goott.user.domain.ReviewImagesDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserReviewDAOImpl implements UserReviewDAO {
	
	@Inject
	private SqlSession ses;

	private static String ns="com.team.mappers.user.review.userReviewMapper.";

	@Override
	public List<ReviewDTO> getAllReviews(ReviewPageDTO paging, String sort){
		// 리뷰 조회
		String orderBy;
		switch (sort) {
			case "score_desc":
				orderBy = "score DESC";
				break;
			case "score_asc":
				orderBy = "score ASC";
				break;
			case "latest":
				orderBy = "createAt DESC";
				break;
			default:
				orderBy = "createAt DESC"; // 기본값
		}

		paging.setOrderBy(orderBy);
		return ses.selectList(ns+"getAllrevws",paging);
	}

	@Override
	public ReviewDTO reviewByNo(int reviewId) {
		// 리뷰 상세 조회
		return ses.selectOne(ns+"getBoardByNo",reviewId);
	}

	@Override
	public int insertReview(ReviewDTO reviewDTO) {
		//리뷰작성
		try {
			return ses.insert(ns + "insertReview", reviewDTO);
		} catch (Exception e) {
			throw new RuntimeException("리뷰 추가 중 오류 발생", e);
		}
	}


	@Override
	public int delReview(int reviewId) {
		// 리뷰 삭제
		return ses.delete(ns+"deleteReview", reviewId);
	}

	@Override
	public int insertImgs(ReviewImagesDTO reviewImg) {
		// 이미지 첨부
	   try {
			return ses.insert(ns + "insertImgs", reviewImg);
		} catch (Exception e) {
			throw new RuntimeException("이미지 추가 중 오류 발생", e);
		}
	}

	@Override
	public List<ReviewImagesDTO> filesByNo(int reviewId) {
		// 상세조회 첨부된 파일 불러오기
		return ses.selectList(ns+"getFileByNo",reviewId);
	}

	@Override
	public int delFiles(int reviewId) {
		// 상세조회 첨부된 파일 삭제하기
		return ses.delete(ns+"deleteFiles",reviewId);
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

	@Override
	public List<ReviewDTO> getMyReviews(ReviewPageDTO paging) {
		// 내 리뷰 가져오기
		return ses.selectList(ns+"getMyReview",paging);
	}

	@Override
	public List<ReserveDTO> getReserveByUserId(int userId) {
		// 예약정보가져오기(userId)
		return ses.selectList(ns+"reserveByUserId", userId);
	}

	@Override
	public List<ReviewDTO> getUserReviews(int userId) {
		// userId로 리뷰들 가져오기
		return ses.selectList(ns+"getUserReviews", userId);
	}

	@Override
	public ReserveDTO getReserveInfo(int reserveId) {
		// reserveId로 예약정보 가져오기
		return ses.selectOne(ns+"getReserveByreserveId", reserveId);
	}

	@Override
	public int checkIfImageExist(int imageId) {
		// imageid로 존재하는 이미지 개수가 1인지 0인지 파악하기
		return ses.selectOne(ns+"countImageId", imageId);
	}

	@Override
	public int changeStatusCodeId(int reserveId, int newStatusCode) {
		//리뷰 작성 시 예약 statusCode를 5로 바꿔준다
		Map<String, Object> reserveMap = new HashMap<String, Object>();
		reserveMap.put("reserveId", reserveId);
		reserveMap.put("statusCodeId", newStatusCode);
		 try {
				return ses.update(ns + "updateStatusCode", reserveMap);
			} catch (Exception e) {
				throw new RuntimeException("상태 코드 변경 중 오류 발생", e);
			}
	}

	@Override
	public ReviewDTO selectUserByUserId(int userId) {
		// userId로 userName&profileUrl을 가져온다
		return ses.selectOne(ns+"selectUserName", userId);
	}


	@Override
	public int setNotification(NotificationDTO notification) {
		return ses.insert(ns+"setNotification", notification);
	}
}
