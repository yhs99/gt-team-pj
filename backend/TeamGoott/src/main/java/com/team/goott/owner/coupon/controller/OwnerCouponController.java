package com.team.goott.owner.coupon.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.coupon.service.OwnerCouponService;
import com.team.goott.owner.domain.CouponDTO;
import com.team.goott.owner.domain.CouponVO;
import com.team.goott.owner.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/coupon")
public class OwnerCouponController {

	@Autowired
	private OwnerCouponService ownerCouponService;

	// 쿠폰 조회: 식당의 쿠폰 목록 조회
	@GetMapping("")
	public ResponseEntity<Object> getCouponsByStoreId(HttpSession session, @RequestParam(value = "sortMethod", required = false, defaultValue = "default") String sort) {
	    
	    StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
	    
	    Map<String, Object> couponMap = new HashMap<String, Object>();
	    int couponCount = 0;
	    List<CouponVO> couponList = new ArrayList<>();  // List 초기화

	    // 사용자 로그인 상태 확인
	    if (storeSession == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
	    }

	    int storeId = storeSession.getStoreId();

	    List<CouponVO> coupons = null;
	    
	    try {
	        // 쿠폰 목록 조회
	        coupons = ownerCouponService.getCouponsByStoreId(storeId);

	        for (CouponVO coupon : coupons) {
	            log.info(coupon + "");
	            if (!coupon.isDeleted()) {
	                couponCount++;
	                couponList.add(coupon);
	            }
	        }

	        couponMap.put("couponCount", couponCount);  // Map에 쿠폰 수 추가
	        couponMap.put("coupons", couponList);       // Map에 쿠폰 리스트 추가

	    } catch (Exception e) {
	        log.error("서버 오류 발생", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생"); // 예외 발생 시 에러 응답
	    }

	    // 쿠폰 목록 반환
	    return ResponseEntity.ok(couponMap); // 성공적으로 조회된 쿠폰 목록 반환
	}
	
	// 쿠폰 생성
	@PostMapping("")
	public ResponseEntity<Object> createCoupon(HttpSession session, CouponDTO coupon) {

		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		
		if (storeSession == null) { // return
			ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
		}
		
		int storeId = storeSession.getStoreId();

		coupon.setStoreId(storeId);

		try {
			int result = ownerCouponService.createCoupon(coupon);
			if (result > 0) {
				return ResponseEntity.ok("쿠폰 생성 성공"); // 성공 응답
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 생성 실패"); // 실패 응답
			}
		} catch (Exception e) {
			log.error("서버 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생"); // 예외 발생 시 응답
		}
	}
	
	 // 쿠폰 삭제
    @DeleteMapping("/{couponId}") // 쿠폰 ID를 경로 변수로 받음
    public ResponseEntity<Object> deleteCoupon(HttpSession session, @PathVariable int couponId) {
    	log.info("컨트롤러 couponId : " + couponId);
        StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
        log.info("컨트롤러 store 세션 : " + storeSession);
        
        if (storeSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
        }
        
        int storeId = storeSession.getStoreId();
        log.info("컨트롤러 storeId : " + storeId);
        
        try {
            int result = ownerCouponService.deleteCoupon(couponId, storeId); // 삭제 메서드 호출
            
            if (result > 0) {
                return ResponseEntity.ok("쿠폰 삭제 성공"); // 성공 응답
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 삭제 실패"); // 실패 응답
            }
        } catch (Exception e) {
            log.error("삭제 권한이 없습니다", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생"); // 예외 발생 시 응답
        }
    }
	
    // 쿠폰 수정
    @PutMapping("/{couponId}")
    public ResponseEntity<Object> updateCoupon(HttpSession session, @PathVariable int couponId, CouponDTO coupon) {
    	
        StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
        
        if (storeSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
        }
        
    	int storeId = storeSession.getStoreId();
    	
    	coupon.setStoreId(storeId);
    	
        try {
        	
            int result = ownerCouponService.modifyCoupon(couponId, storeId, coupon);
            
            if (result > 0) {
                return ResponseEntity.ok("쿠폰 수정 성공"); // 성공 응답
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 수정 실패"); // 실패 응답
            }
        } catch (Exception e) {
            log.error("서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생"); // 예외 발생 시 응답
        }
    }
}
