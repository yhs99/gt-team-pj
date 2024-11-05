package com.team.goott.user.reserve.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.PayHistoryDTO;
import com.team.goott.user.domain.ReserveDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserReserveDAOImpl implements UserReserveDAO {

	@Inject
	private SqlSession ses;

	private static String ns = "com.team.mappers.user.reserve.userReserveMapper.";

	@Override
	public List<CartDTO> getCartCheck(int userId) {
		return ses.selectList(ns + "getCartCheck", userId);
	}

	@Override
	public int insertReserve(int userId, ReserveDTO reserveDTO) {
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		params.put("reserveDTO", reserveDTO);
		return ses.insert(ns + "insertReserve", params);
	}

	@Override
	public int getMaxPeople(int storeId) {
		return ses.selectOne(ns + "getMaxPeople", storeId);
	}

	@Override
	public int getTimeTotPeople(int storeId, LocalDateTime reserveTime) {
		Map<String, Object> params = new HashMap<>();
		params.put("storeId", storeId);
		params.put("reserveTime", reserveTime);
		return ses.selectOne(ns + "getTimeTotPeople", params);
	}

	@Override
	public int getMaxPeoplePerReserve(int storeId) {
		return ses.selectOne(ns + "getMaxPeoplePerReserve", storeId);
	}

	@Override
	public Object updateReserved(int storeId, LocalDateTime reserveTime) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("storeId", storeId);
		params.put("reserveTime", reserveTime);
		return ses.update(ns + "updateReserved", params);
	}

	@Override
	public List<MenuDTO> getStoreMenuById(int menuId, int storeId) {
		Map<String, Object> params = new HashMap<>();
		params.put("menuId", menuId);
		params.put("storeId", storeId);
		return ses.selectList(ns + "getStoreMenuById", params);
	}

	@Override
	public int updateReserve(int reserveId, int userId) {
		Map<String, Object> params = new HashMap<>();
		params.put("reserveId", reserveId);
		params.put("userId", userId);
		return ses.update(ns+"updateReserve",params);
	}

	@Override
	public int deleteCart(int userId) {
		return ses.delete(ns+"deleteCart",userId);
				
	}

	@Override
	public int getCouponDiscount(int couponId) {
		return ses.selectOne(ns+"getCouponDiscount",couponId);
		
	}

	@Override
	public int updateCouponStock(int couponId) {
		return ses.update(ns+"updateCouponStock",couponId);
		
	}

	@Override
	public int insertPayHistory(PayHistoryDTO payHistoryDTO) {
		return ses.insert(ns+"insertPayHistory", payHistoryDTO);
	}

	@Override
	public Integer getSlotCheck(int storeId, LocalDateTime reserveTime)throws Exception {
		 if (reserveTime == null) {
		        log.error("예약 시간(reserveTime)이 null입니다.");
		        return 0; // 기본값으로 예약 가능 상태를 반환
		    }
		    Map<String, Object> params = new HashMap<>();
		    params.put("storeId", storeId);
		    params.put("reserveTime", reserveTime);
		    
		    Integer result = ses.selectOne(ns + "getSlotCheck", params);
		    return result != null ? result : 0;
		}

	@Override
	public int getCouponStockCheck(int couponId) {
		return ses.selectOne(ns+"getCouponStockCheck",couponId);
	}

	@Override
	public LocalDateTime getCouponStartTime(int couponId) throws Exception {
		return ses.selectOne(ns+"getCouponStartTime", couponId);
	}

	@Override
	public LocalDateTime getCouponEndTime(int couponId) throws Exception {
		return ses.selectOne(ns+"getCouponEndTime", couponId);
	}

	@Override
	public Timestamp getReserveTime(int reserveId, int userId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("reserveId", reserveId);
		params.put("userId", userId);
		return ses.selectOne(ns+"getReserveTime",params);
	}

	@Override
	public int getStoreId(int reserveId, int userId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("reserveId", reserveId);
		params.put("userId", userId);
		return ses.selectOne(ns+"getStoreId",params);
	}

	@Override
	public int getUpdateReserveSlotReserved(LocalDateTime reserveTime, int storeId) {
		Map<String, Object> params = new HashMap<>();
		params.put("reserveTime", reserveTime);
		params.put("storeId", storeId);
		return ses.update(ns+"getUpdateReserveSlotReserved",params);
	}

	@Override
	public Integer getCouponStoreId(Integer couponId) throws Exception {
		return ses.selectOne(ns+"getCouponStoreId", couponId);
	}




}
