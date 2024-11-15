package com.team.goott.owner.coupon.service;

import java.time.LocalDate;
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
		log.info("서비스단 넘어온 coupon : " + coupon.toString());

		if (existingCoupon != null && existingCoupon.getStoreId() == storeId) {
			validateAndSetDefaults(coupon);
			log.info("서비스단 유효성 검사 후 coupon : " + coupon.toString());

			Map<String, Object> couponData = new HashMap<>();
			couponData.put("couponId", couponId);
			couponData.put("storeId", storeId);
			
			if (coupon.getStock() < 101) {
				couponData.put("stock", coupon.getStock());
			}
			

			if (!existingCoupon.getCouponName().equals(coupon.getCouponName())) {
				couponData.put("couponName", coupon.getCouponName());
			}

			if (existingCoupon.getDiscount() != coupon.getDiscount()) {
				if (coupon.getDiscount() == 0) {
					coupon.setDiscount(null);
				}
				couponData.put("discount", coupon.getDiscount());
			}

			if (existingCoupon.getStock() != coupon.getStock()) {
				log.info("DB의 쿠폰 stock : " + existingCoupon.getStock());
				log.info("넘어온 coupon의 stock : " + coupon.getStock());
				couponData.put("stock", coupon.getStock());
			}

			if (existingCoupon.getStart() == null || !existingCoupon.getStart().equals(coupon.getStart())) {
				couponData.put("start", coupon.getStart());
			}

			if (existingCoupon.getEnd() == null || !existingCoupon.getEnd().equals(coupon.getEnd())) {
				couponData.put("end", coupon.getEnd());
			}
			
			log.info("서비스단 dao 가기전 coupon : " + coupon.toString());
			log.info("서비스단 couponMap의 데이터 : " + couponData.toString());

			return ownerCouponDAO.modifyCoupon(couponData); // 1 반환 시 성공
		} else {
			throw new Exception("쿠폰 수정 권한이 없습니다."); // 권한 없음 예외 처리
		}
	}

	private void validateAndSetDefaults(CouponDTO coupon) {

		
	    boolean hasStartDate = coupon.getStart() != null;
	    boolean hasEndDate = coupon.getEnd() != null;
	    boolean hasStock = coupon.getStock() > 0;
	    
		LocalDate startDate = coupon.getStart();
		LocalDate endDate = coupon.getEnd();
		
		if (hasStartDate && hasEndDate) {
			if(startDate.isAfter(endDate)) {
				throw new IllegalArgumentException("시작일을 종료일보다 늦게 설정할 수 없습니다");
			}
		}

	    // 날짜와 수량이 모두 설정된 경우 예외 처리
	    if (hasStartDate && hasEndDate && hasStock) {
	        throw new IllegalArgumentException("날짜와 수량을 동시에 설정할 수 없습니다. 하나만 설정하십시오.");
	    }

	    // 날짜만 설정된 경우 stock을 -1로 설정
	    if (hasStartDate && hasEndDate && !hasStock) {
	        coupon.setStock(-1);
	        return; // 더 이상 검사를 진행하지 않음
	    }

	    // 수량만 설정된 경우 날짜를 null로 설정
	    if (!hasStartDate && !hasEndDate && hasStock) {
	        coupon.setStart(null);
	        coupon.setEnd(null);
	        return; // 더 이상 검사를 진행하지 않음
	    }

	    // 날짜와 재고가 모두 설정되지 않은 경우 예외 처리
	    if (!hasStartDate && !hasEndDate && !hasStock) {
	        throw new IllegalArgumentException("날짜와 수량 중 하나는 반드시 설정해야 합니다.");
	    }

	    // 재고가 설정되지 않은 경우 예외 처리
	    if (!hasStock) {
	        throw new IllegalArgumentException("수량을 입력하십시오.");
	    }

	    // 날짜가 설정되지 않은 경우 예외 처리
	    if (!hasStartDate && !hasEndDate) {
	        throw new IllegalArgumentException("날짜를 입력하십시오.");
	    }
	}

}
