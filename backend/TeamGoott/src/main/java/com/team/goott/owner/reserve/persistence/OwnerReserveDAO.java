package com.team.goott.owner.reserve.persistence;

import java.time.LocalDateTime;
import java.util.List;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.NotificationType;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.UserDTO;

public interface OwnerReserveDAO {

	List<ReserveDTO> getAllReserve(int storeId, String sortMethod);

	int getTotalReserve(int storeId);

	int getTotalTodayReserve(int storeId);

	int updatestatus(int reserveId, int statusCode);

	ReserveDTO getReserve(int reserveId);

	ReserveSlotsDTO getReserveSlots(int storeId, LocalDateTime reserveTime);

	UserDTO getUser(int userId);

	StoreVO getStore(int storeId);

	int setNotification(NotificationDTO notification);

	List<NotificationDTO> getNotification(int storeId, NotificationType type);

	int updateNotification(int alarmId);

}
