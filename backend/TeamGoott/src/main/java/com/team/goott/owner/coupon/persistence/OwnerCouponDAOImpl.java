package com.team.goott.owner.coupon.persistence;

import java.util.List;

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
	public int deleteCoupon(int couponId) throws Exception {
		
		return ses.delete(ns + "deleteCoupon", couponId);
	}

}
