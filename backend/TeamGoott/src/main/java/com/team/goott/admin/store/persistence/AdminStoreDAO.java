package com.team.goott.admin.store.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.RotationVO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.domain.SummaryTitleDTO;
import com.team.goott.admin.domain.SummaryVO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface AdminStoreDAO {
	List<StoreDTO> getAllStoreLists();
	List<ScheduleDTO> getScheduleByDayCode(int dayCode);
	RotationVO getRotationCodeIdByStoreId(int storeId);
	List<ReserveSlotsDTO> getExistingSlots(Map<String, Object> map);
	int insertReserveSlot(ReserveSlotsDTO newSlot);
	int batchInsertSlots(List<ReserveSlotsDTO> slots);
	List<StoresVO> getStoresInfo(Map<String, Object> searchMap);
	int isExistStore(Map<String, Integer> map);
	int blockStore(int storeId);
	int cancelBlock(int storeId);
	SummaryTitleDTO getSummaryTitle(int storeId);
	List<SummaryVO> getDailySales(int storeId);
	List<SummaryVO> getMonthlySales(int storeId);
	StoresVO getStoreInfoForUpdate(Map<String, Object> map);
	LocalDate getLastReserveSlot(int storeId);
}
