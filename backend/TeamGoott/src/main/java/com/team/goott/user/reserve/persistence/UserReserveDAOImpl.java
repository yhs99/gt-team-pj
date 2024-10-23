package com.team.goott.user.reserve.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;
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
		log.info("Inserting reserve with storeId: {}", reserveDTO.getStoreId());
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
		return ses.delete(ns+"updateReserve",params);
	}

}
