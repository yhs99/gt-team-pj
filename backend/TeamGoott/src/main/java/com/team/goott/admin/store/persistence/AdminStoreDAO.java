package com.team.goott.admin.store.persistence;

import java.time.LocalDate;
import java.util.List;

import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;

public interface AdminStoreDAO {
	List<ScheduleDTO> getScheduleByDayCode(int dayCode);
	int getRotationCodeIdByStoreId(int storeId);
	List<ReserveSlotsDTO> getExistingSlots(LocalDate date);
	int insertReserveSlot(ReserveSlotsDTO newSlot);
}
