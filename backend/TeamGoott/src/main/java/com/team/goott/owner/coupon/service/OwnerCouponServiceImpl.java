package com.team.goott.owner.coupon.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	    	
	        // couponName과 discount가 null이 아닌 경우에만 쿠폰 생성
	        if (coupon.getCouponName() != null && coupon.getDiscount() != null) {
	            validateAndSetDefaults(coupon);
	            parseCouponDates(coupon);
	            return ownerCouponDAO.createCoupon(coupon);
	        } else {
	            throw new IllegalArgumentException("쿠폰 이름과 할인율은 반드시 설정되어야 합니다."); // 예외 처리
	        }
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
	            
	            Map<String, Object> couponData = new HashMap<>();
	            couponData.put("couponId", couponId);
	            couponData.put("storeId", storeId);

	            if (!existingCoupon.getCouponName().equals(coupon.getCouponName())) {
	                couponData.put("couponName", coupon.getCouponName());
	            }
	            
	            if (existingCoupon.getDiscount() != coupon.getDiscount()) {
	                couponData.put("discount", coupon.getDiscount());
	            }
	            
	            if (existingCoupon.getStock() != coupon.getStock()) {
	                couponData.put("stock", coupon.getStock());
	            }
	            
	            if (existingCoupon.getStart() == null || !existingCoupon.getStart().equals(coupon.getStart())) {
	                couponData.put("start", coupon.getStart());
	            }

	            if (existingCoupon.getEnd() == null || !existingCoupon.getEnd().equals(coupon.getEnd())) {
	                couponData.put("end", coupon.getEnd());
	            }

	                return ownerCouponDAO.modifyCoupon(couponData); // 1 반환 시 성공
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

	 // 쿠폰의 날짜를 지정하면 수량을 무제한으로 변경하고
	 // 쿠폰의 수량을 입력하면 기한을 무제한으로 지정하는 메서드
	    private void validateAndSetDefaults(CouponDTO coupon) {
	        boolean hasStartDate = coupon.getStart() != null;
	        boolean hasEndDate = coupon.getEnd() != null;
	        
	        if (coupon.getStock() == null) {
	            coupon.setStock(0);
	        }
	        
	        boolean hasStock = coupon.getStock() > 0;

	        // 날짜와 수량이 모두 설정된 경우 예외 처리
	        if (hasStartDate && hasEndDate && hasStock) {
	            throw new IllegalArgumentException("날짜 또는 수량 중 하나만 설정할 수 있습니다.");
	        }

	        // 날짜가 설정되어 있으면 수량을 무제한으로 설정하지 않음
	        if (hasStartDate || hasEndDate) {
	            if (hasStock) {
	                throw new IllegalArgumentException("날짜가 설정된 경우 수량은 입력할 수 없습니다.");
	            }
	        } else if (hasStock) {
	            // 수량이 설정되어 있으면 날짜를 무제한으로 설정
	            coupon.setStart(null);
	            coupon.setEnd(null);
	        } else {
	            // 수량도 없고 날짜도 없으면 예외 처리
	            throw new IllegalArgumentException("수량이나 날짜 중 하나는 반드시 설정되어야 합니다.");
	        }
	    }
	 
	}
