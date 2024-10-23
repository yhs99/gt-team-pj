package com.team.goott.owner.coupon.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

		@Autowired
	    private OwnerCouponDAO ownerCouponDAO;

	    // 쿠폰 조회
	    @Override
	    public List<CouponVO> getCouponsByStoreId(int storeId) throws Exception {
	        return ownerCouponDAO.selectCouponsByStoreId(storeId);
	    }
	    
	    // 쿠폰 생성
	    @Override
	    public int createCoupon(CouponDTO coupon) throws Exception {
	        validateAndSetDefaults(coupon);
	        return ownerCouponDAO.createCoupon(coupon);
	    }

	    // 쿠폰 삭제
	    @Override
	    @Transactional(rollbackFor = Exception.class)
	    public int deleteCoupon(int couponId, int storeId) throws Exception {
	        CouponVO coupon = ownerCouponDAO.selectCouponByCouponId(couponId);

	        if (coupon != null && coupon.getStoreId() == storeId) {
	            return ownerCouponDAO.deleteCoupon(couponId); // 1 반환 시 성공
	        } else {
	            throw new Exception("쿠폰 삭제 권한이 없습니다. "); // 권한 없음 예외 처리
	        }
	    }

	    // 쿠폰 수정
	    @Override
	    @Transactional(rollbackFor = Exception.class)
	    public int modifyCoupon(int couponId, int storeId, CouponDTO coupon) throws Exception {
	        CouponVO existingCoupon = ownerCouponDAO.selectCouponByCouponId(couponId);

	        if (existingCoupon != null && existingCoupon.getStoreId() == storeId) {
	            parseCouponDates(coupon);
	            validateAndSetDefaults(coupon);
	            return ownerCouponDAO.modifyCoupon(couponId, coupon); // 1 반환 시 성공
	        } else {
	            throw new Exception("쿠폰 수정 권한이 없습니다."); // 권한 없음 예외 처리
	        }
	    }

	    // 쿠폰의 시작, 종료 날짜를 맞는 타입으로 변환하는 메서드
	    private void parseCouponDates(CouponDTO coupon) {
	        if (coupon.getStart() != null) {
	            coupon.setStart(LocalDateTime.parse(coupon.getStart().toString(), DateTimeFormatter.ISO_DATE_TIME));
	        }
	        if (coupon.getEnd() != null) {
	            coupon.setEnd(LocalDateTime.parse(coupon.getEnd().toString(), DateTimeFormatter.ISO_DATE_TIME));
	        }
	    }

	    // 쿠폰의 날짜를 지정하면 수량을 무제한으로 변경해주고
	    // 쿠폰의 수량을 입력하면 기한을 무제한으로 지정하는 메서드
	    private void validateAndSetDefaults(CouponDTO coupon) {
	        if (coupon.getStock() == 0) {
	            if (coupon.getStart() == null || coupon.getEnd() == null) {
	                throw new IllegalArgumentException("시작날짜와 종료날짜는 필수입니다.");
	            }
	            coupon.setStock(Integer.MAX_VALUE); // stock이 0인 경우 무제한으로 설정
	        } else {
	            coupon.setEnd(null); // stock이 입력되었으면 end 값을 무제한으로 설정
	        }
	    }
	}
