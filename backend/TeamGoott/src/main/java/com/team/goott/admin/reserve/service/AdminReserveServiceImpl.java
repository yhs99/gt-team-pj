package com.team.goott.admin.reserve.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.goott.admin.domain.ReservesVO;
import com.team.goott.admin.reserve.persistence.AdminReserveDAO;
import com.team.goott.owner.reserve.service.OwnerReserveServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminReserveServiceImpl implements AdminReserveService {

	@Autowired
	private AdminReserveDAO adminReserveDAO;
	@Autowired
	private OwnerReserveServiceImpl ownerReserveService;
	
	@Override
	public List<ReservesVO> getReserveLists(Map<String, Object> filters) {
		List<ReservesVO> ls = adminReserveDAO.getReserveLists(filters);
		log.info(ls.toString());
		try {
			ls.stream()
			.forEach(ReservesVO::setPrices);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("에러");
		}
		return ls;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateReserveStatusCode(List<Integer> reserveId, int statusCodeId) {
		int result=0;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("reserveIds", reserveId);
			map.put("statusCodeId", statusCodeId);
			result = adminReserveDAO.updateStatus(map);
			
			if(result >= 1) {
				//이메일 알림 보내기
				reserveId.stream().forEach(rId->ownerReserveService.sendEmailNotification(rId));
				reserveId.stream().forEach(rId->ownerReserveService.sendNotificationToUser(rId));
				return result;
			} else {
				throw new Exception("업데이트 도중 에러가 발생했습니다.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
