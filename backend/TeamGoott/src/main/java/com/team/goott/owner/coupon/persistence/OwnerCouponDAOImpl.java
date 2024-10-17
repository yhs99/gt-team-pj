package com.team.goott.owner.coupon.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.CouponDTO;
import com.team.goott.owner.domain.CouponVO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerCouponDAOImpl implements OwnerCouponDAO {
	
	@Autowired
	SqlSession ses;
	
	private String ns = "com.team.mappers.owner.coupon.ownerCouponMapper.";
	
	@Override
	public List<CouponVO> selectCouponsByStoreId(int storeId) throws Exception {
	
		return ses.selectList(ns + "selectCouponsByStoreId", storeId);
	}

	@Override
	public int createCoupon(CouponDTO coupon) {
		
		return ses.insert(ns + "createCoupon", coupon);
		
	}


	@Override
	public CouponVO selectCouponByCouponId(int couponId) throws Exception {
		
		return ses.selectOne(ns + "selectCouponsByCouponId", couponId); 
	}


	@Override
	public int deleteCoupon(int couponId) throws Exception {
		return ses.update(ns + "deleteCoupon", couponId);
	}

	@Override
	public int modifyCoupon(int couponId, CouponDTO coupon) throws Exception {
	    log.info("다오단 (수정) : " + couponId);

	    // 쿠폰 정보를 Map에 담기
	    Map<String, Object> params = new HashMap<>();
	    params.put("couponId", couponId); // couponId 추가
	    params.put("coupon", coupon); // CouponDTO 추가

	    // 매퍼에 Map을 전달하여 업데이트 수행
	    return ses.update(ns + "modifyCoupon", params);
	}



}
