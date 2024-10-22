package com.team.goott.owner.reserve.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.team.goott.infra.SendEmailService;
import com.team.goott.owner.domain.NotificationDTO;
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
//			sendEmailNotification(reserveId);
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
		ReserveDTO reserve = reserveDAO.getReserve(reserveId);
		StoreVO store = reserveDAO.getStore(reserve.getStoreId());
		int userId = reserve.getUserId();
		UserDTO user = reserveDAO.getUser(userId);
		log.info("user email : " +  user.getEmail());
		
		SendEmailService emailService = new SendEmailService();
		
		try {
			emailService.sendMail(reserve, user, store);
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		
	}
	

	private int sendNotificationToUser(int reserveId) {
		ReserveDTO reserve = reserveDAO.getReserve(reserveId);
		int userId = reserve.getUserId();
		UserDTO user = reserveDAO.getUser(userId);
		
		NotificationDTO notification = new NotificationDTO();
		notification.setUserId(user.getUserId());
		notification.setMessage("예약 변경 사항이 있습니다 메일을 확인해 주세요");
		
		log.info("{}", notification.toString());
		
		return reserveDAO.setNotification(notification);
		
	}


	@Override
	public List<NotificationDTO> getNotification(int userId) {
		return reserveDAO.getNotification(userId);
	}


	@Override
	public int updateNotification(int alarmId) {
		return reserveDAO.updateNotification(alarmId);
	}
}
