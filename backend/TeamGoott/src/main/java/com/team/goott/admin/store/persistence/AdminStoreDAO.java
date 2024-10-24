package com.team.goott.admin.store.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreDTO;

public interface AdminStoreDAO {
	List<StoreDTO> getAllStoreLists();
	List<ScheduleDTO> getScheduleByDayCode(int dayCode);
	int getRotationCodeIdByStoreId(int storeId);
	List<ReserveSlotsDTO> getExistingSlots(Map<String, Object> map);
	int insertReserveSlot(ReserveSlotsDTO newSlot);
	int batchInsertSlots(List<ReserveSlotsDTO> slots);
}
