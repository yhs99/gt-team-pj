package com.team.goott.admin.store.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AdminStoreDAOImpl implements AdminStoreDAO {
	
	private final static String NS = "com.team.mappers.admin.store.adminStoreMapper.";
	
	@Inject
	private SqlSession ses;
	
	@Override
	public List<ScheduleDTO> getScheduleByDayCode(int dayCode) {
		return ses.selectList(NS+"getScheduleByDayCode", dayCode);
	}

	@Override
	public int getRotationCodeIdByStoreId(int storeId) {
		return ses.selectOne(NS+"getRotationCodeIdByStoreId", storeId);
	}

	@Override
	public List<ReserveSlotsDTO> getExistingSlots(Map<String, Object> map) {
		return ses.selectList(NS+"getExistingSlots", map);
	}

	@Override
	public int insertReserveSlot(ReserveSlotsDTO newSlot) {
		return ses.insert(NS+"insertReserveSlot", newSlot);
	}

	@Override
	public List<StoreDTO> getAllStoreLists() {
		return ses.selectList(NS+"getAllStoreLists");
	}

	@Override
	public int batchInsertSlots(List<ReserveSlotsDTO> slots) {
		return ses.insert(NS+"batchInsertSlots", slots);
	}

}
