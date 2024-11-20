package com.team.goott.admin.store.persistence;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.admin.domain.RotationVO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.domain.SummaryTitleDTO;
import com.team.goott.admin.domain.SummaryVO;
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
	public RotationVO getRotationCodeIdByStoreId(int storeId) {
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

	@Override
	public List<StoresVO> getStoresInfo(Map<String, Object> searchMap) {
		return ses.selectList(NS+"getStoresInfo", searchMap);
	}

	@Override
	public int isExistStore(Map<String, Integer> map) {
		return ses.selectOne(NS+"isExistStore", map);
	}

	@Override
	public int blockStore(int storeId) {
		return ses.update(NS+"blockStore", storeId);
	}

	@Override
	public int cancelBlock(int storeId) {
		return ses.update(NS+"cancelBlock", storeId);
	}

	@Override
	public SummaryTitleDTO getSummaryTitle(int storeId) {
		return ses.selectOne(NS+"getSummaryTitle", storeId);
	}

	@Override
	public List<SummaryVO> getDailySales(int storeId) {
		return ses.selectList(NS+"getDailySales", storeId);
	}

	@Override
	public List<SummaryVO> getMonthlySales(int storeId) {
		return ses.selectList(NS+"getMonthlySales", storeId);
	}

	@Override
	public StoresVO getStoreInfoForUpdate(Map<String, Object> map) {
		List<StoresVO> storeInfo = new ArrayList<StoresVO>();
		storeInfo = ses.selectList(NS+"getStoresInfo", map);
		return storeInfo.get(0);
	}

	@Override
	public LocalDate getLastReserveSlot(int storeId) {
		return ses.selectOne(NS+"getLastReserveSlot", storeId);
	}

}
