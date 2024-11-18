package com.team.goott.owner.reserve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.ReserveInfoVO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.user.domain.ReserveDTO;

public interface OwnerReserveService {

	ReserveInfoVO getAllReserveInfo(int storeId, String sortMethod);

	int updateStatus(int reserveId, int statusCode, int storeId);

	ReserveDTO getReserve(int reserveId);

	List<ReserveSlotsDTO>  getReserveSlots(int storeId, LocalDate reserveTime);

	List<NotificationDTO> getNotification(int storeId);

	int updateNotification(int alarmId);

}
