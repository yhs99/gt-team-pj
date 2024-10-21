package com.team.goott.owner.reserve.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.ReserveDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerReserveDAOImpl implements OwnerReserveDAO {
	
	@Inject
	SqlSession session;
	private static String ns = "com.team.mappers.owner.reserve.ownerReserveMapper.";
	
	@Override
	public List<ReserveDTO> getAllReserve(int storeId, String sortMethod) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("storeId", storeId);
		args.put("sortMethod", sortMethod);
		return session.selectList(ns+"getAllReserve", args);
	}

	@Override
	public int getTotalReserve(int storeId) {
		return session.selectOne(ns+"getTotalReserve", storeId);
	}

	@Override
	public int getTotalTodayReserve(int storeId) {
		return session.selectOne(ns+"getTotalTodayReserve", storeId);
	}

	@Override
	public int updatestatus(int reserveId, int statusCode) {
		Map<String, Integer> args = new HashMap<String,Integer>();
		args.put("reserveId", reserveId);
		args.put("statusCode", statusCode);
		return session.update(ns+"updateStatus", args);
	}

}
