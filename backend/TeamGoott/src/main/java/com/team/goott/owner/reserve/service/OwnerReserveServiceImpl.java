package com.team.goott.owner.reserve.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.team.goott.infra.SendEmailService;
import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.NotificationType;
import com.team.goott.owner.domain.ReserveInfoVO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.StoreVO;
import com.team.goott.owner.reserve.persistence.OwnerReserveDAO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerReserveServiceImpl implements OwnerReserveService {
	
	@Inject
	OwnerReserveDAO reserveDAO;
	
	
	@Override
	public ReserveInfoVO getAllReserveInfo(int storeId, String sortMethod) {
		List<ReserveDTO> reserveList = reserveDAO.getAllReserve(storeId, sortMethod);
		int totalReserve = reserveDAO.getTotalReserve(storeId);
		int totalTodayReserve = reserveDAO.getTotalTodayReserve(storeId);
		
		 return ReserveInfoVO.builder().totalReserve(totalReserve).totalTodayReserve(totalTodayReserve).reservations(reserveList).build();
	}


	@Override
	public int updateStatus(int reserveId, int statusCode) {
		int result = reserveDAO.updatestatus(reserveId, statusCode);
		
		if(result == 1) {
			//이메일 알림 보내기
			sendEmailNotification(reserveId);
			// 회원페이지 알림 보내기 
			int sendNotification = sendNotificationToUser(reserveId);
			if(sendNotification == 1) {
				log.info("유저 알림 설정 완료");
			}
			return result;
		} else {
			log.info("예약 업데이트 실패");
			return 0;
		}
	}






	@Override
	public ReserveDTO getReserve(int reserveId) {
		return reserveDAO.getReserve(reserveId);
	}


	@Override
	public ReserveSlotsDTO getReserveSlots(int storeId, LocalDateTime reserveTime) {
		return reserveDAO.getReserveSlots(storeId, reserveTime);
	}

	private void sendEmailNotification(int reserveId) {
		// 유저 정보, 예약 식당 정보, 예약 정보 
		ReserveDTO reserve = reserveDAO.getReserve(reserveId);
		StoreVO store = reserveDAO.getStore(reserve.getStoreId());
		int userId = reserve.getUserId();
		UserDTO user = reserveDAO.getUser(userId);
		
		SendEmailService emailService = new SendEmailService();
		
		try {
			emailService.sendMail(reserve, user, store);
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		
	}
	

	private int sendNotificationToUser(int reserveId) {
		
		//알림을 보낼 유저, 예약 변경 사항 정보
		ReserveDTO reserve = reserveDAO.getReserve(reserveId);
		StoreVO store = reserveDAO.getStore(reserve.getStoreId());
		int userId = reserve.getUserId();
		int storeId = reserve.getStoreId();
		UserDTO user = reserveDAO.getUser(userId);
		NotificationType notificationType = NotificationType.valueOf("OWNER_TO_CUSTOMER");
		
		NotificationDTO notification = new NotificationDTO();
		notification.setUserId(user.getUserId());
		notification.setStoreId(storeId);
		notification.setNotificationType(notificationType);
		notification.setReserveId(reserveId);

		// 에약 상태에 따른 알림 메세지 설정
		switch (reserve.getStatusCodeId()) {
		case 1:
			notification.setMessage("예약하신 "+store.getStoreName() + "에 대한 예약 상태가 업데이트 되었습니다 : " + "예약대기" );
			break;
		case 2:
			notification.setMessage("예약하신 "+store.getStoreName() + "에 대한 예약 상태가 업데이트 되었습니다 : " + "예약승인" );
			break;
		case 3:
			notification.setMessage("예약하신 "+store.getStoreName() + "에 대한 예약 상태가 업데이트 되었습니다 : " + "예약취소" );
			break;
		case 4:
			notification.setMessage("예약 방문하신 가게에 대한 리뷰를 남겨주세요!" );
			break;
		}
		
		log.info("{}", notification.toString());
		
		// 알림 테이블에 insert
		return reserveDAO.setNotification(notification);
		
	}


	@Override
	public List<NotificationDTO> getNotification(int storeId) {
		return reserveDAO.getNotification(storeId);
	}


	@Override
	public int updateNotification(int alarmId) {
		return reserveDAO.updateNotification(alarmId);
	}
}
