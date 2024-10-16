package com.team.goott.owner.coupon.persistence;

import java.util.List;

import com.team.goott.owner.domain.CouponDTO;
import com.team.goott.owner.domain.CouponVO;

public interface OwnerCouponDAO {

	// 쿠폰 목록을 불러오는 메서드
	List<CouponVO> selectCouponsByStoreId(int storeId) throws Exception;

	// 쿠폰 생성
	int createCoupon(CouponDTO coupon) throws Exception;

	int deleteCoupon(int couponId) throws Exception;

}
