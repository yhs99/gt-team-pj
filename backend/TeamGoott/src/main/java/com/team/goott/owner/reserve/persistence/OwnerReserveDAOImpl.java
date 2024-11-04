package com.team.goott.owner.reserve.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.UserDTO;

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

	@Override
	public ReserveDTO getReserve(int reserveId) {
		return session.selectOne(ns+"getReserve", reserveId);
	}

	@Override
	public ReserveSlotsDTO getReserveSlots(int storeId, LocalDateTime reserveTime) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("storeId", storeId);
		args.put("reserveTime", reserveTime);
		return session.selectOne(ns+"getReserveSlot", args);
	}

	@Override
	public UserDTO getUser(int userId) {
		return session.selectOne(ns+"getUser", userId);
	}

	@Override
	public StoreVO getStore(int storeId) {
		return session.selectOne(ns+"getStore", storeId);
	}

	@Override
	public int setNotification(NotificationDTO notification) {
		return session.insert(ns+"insertNotification", notification);
	}

	@Override
	public List<NotificationDTO> getNotification(int storeId) {
		return session.selectList(ns+"getNotification", storeId);
	}

	@Override
	public int updateNotification(int alarmId) {
		return session.update(ns+"updateNotification", alarmId);
	}


}
