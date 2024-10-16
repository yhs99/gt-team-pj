package com.team.goott.owner.coupon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	// 쿠폰을 삭제하는 메서드
	@Override
	@Transactional(rollbackFor = Exception.class	)
	public int deleteCoupon(int couponId, int storeId) throws Exception {
//		log.info("서비스단 couponId :" + couponId + " storeId : " + storeId);
	    // 1. 특정 쿠폰의 정보를 select 해온다
	    CouponVO selectCoupon = ownerCouponDAO.selectCouponByCouponId(couponId);
	    
//	    log.info("서비스단 selectCoupon : " + selectCoupon);
	    
	    // 2. select 쿠폰의 storeId와 session 저장된 storeId 비교한다
	    if (selectCoupon != null && selectCoupon.getStoreId() == storeId) {
	        // 3. 일치하다면 coupon의 isDeleted의 상태를 UPDATE해준다.
//	    	log.info("서비스단 쿠폰ID로 가져온 storeId :" + selectCoupon.getStoreId() + " storeId : " + storeId);
	        int result = ownerCouponDAO.deleteCoupon(couponId); // 쿠폰 업데이트

	        if (result == 1) {
	            // 삭제 성공 시 1 반환
	            return 1;
	        } else {
	            // 삭제 실패 시 0 반환
	            return 0;
	        }
	    } else {
	        // 4. 만약 일치하지 않다면 throw error 발생시킨다
	        throw new Exception("쿠폰 삭제 권한이 없습니다."); // 권한 없음 예외 처리
	    }
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
	
}
