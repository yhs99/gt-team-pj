package com.team.goott.owner.coupon.service;

import java.util.List;

import com.team.goott.owner.domain.CouponDTO;
import com.team.goott.owner.domain.CouponVO;

public interface OwnerCouponService {

	// 점주 페이지에서 가게ID로 쿠폰목록을 불러오는 메서드
	List<CouponVO> getCouponsByStoreId(int storeId) throws Exception;
	
	// 쿠폰 생성
	int createCoupon(CouponDTO coupon) throws Exception;

	// 쿠폰 삭제
	int deleteCoupon(int couponId, int storeId) throws Exception;

	// 쿠폰 수정
	int modifyCoupon(int couponId, int storeId, CouponDTO coupon) throws Exception;

	

}
