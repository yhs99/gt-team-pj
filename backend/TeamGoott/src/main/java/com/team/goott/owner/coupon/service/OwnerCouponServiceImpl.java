package com.team.goott.owner.coupon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.owner.coupon.persistence.OwnerCouponDAO;
import com.team.goott.owner.domain.CouponDTO;
import com.team.goott.owner.domain.CouponVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerCouponServiceImpl implements OwnerCouponService {
	
	// 점주 쿠폰DAO 객체 주입
	@Autowired
	private OwnerCouponDAO ownerCouponDAO;
	
	// 점주 페이지에서 가게ID로 쿠폰목록을 불러오는 메서드
	@Override
	public List<CouponVO> getCouponsByStoreId(int storeId) throws Exception {
		
		List<CouponVO> couponList = ownerCouponDAO.selectCouponsByStoreId(storeId);
		
		return couponList;
	}

	// 쿠폰을 생성하는 메서드
	@Override
    public int createCoupon(CouponDTO coupon) throws Exception {
        validateAndSetDefaults(coupon);
        // DAO 호출하여 쿠폰 저장 로직 구현
        return ownerCouponDAO.createCoupon(coupon);
    }
	
	
	
	private void validateAndSetDefaults(CouponDTO coupon) {
	    // stock이 존재하지 않을 경우에만 시작날짜와 종료날짜를 검증
	    if (coupon.getStock() == 0) {
	        if (coupon.getStart() == null || coupon.getEnd() == null) {
	            throw new IllegalArgumentException("시작날짜와 종료날짜는 필수입니다.");
	        }
	        coupon.setStock(1000000); // stock이 0인 경우 1000000으로 설정
	    }

	    // stock이 0이 아닌 경우 end 값을 무제한으로 설정 (null)
	    if (coupon.getStock() != 0) {
	        coupon.setEnd(null); // stock이 입력되었으면 end 값을 무제한으로 설정 (null)
	    }
	}

	@Override
	public int deleteCoupon(int couponId) throws Exception {
		
		return ownerCouponDAO.deleteCoupon(couponId);
		
	}
}
