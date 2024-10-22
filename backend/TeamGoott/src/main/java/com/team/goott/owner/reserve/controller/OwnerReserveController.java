package com.team.goott.owner.reserve.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.ReserveInfoVO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.reserve.service.OwnerReserveService;
import com.team.goott.user.domain.ReserveDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/owner")
public class OwnerReserveController {
	
	@Inject
	OwnerReserveService service;
	
	//예약 목록 불러오기
	@GetMapping("/reserve")
	public ResponseEntity<Object> getAllReservation(@RequestParam(value = "sortMethod", defaultValue = "newest") String sortMethod , HttpSession session) {
		log.info("예약목록 API 기능 동작");
		ReserveInfoVO reserveInfo = null;
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			reserveInfo = service.getAllReserveInfo(storeId, sortMethod);
			
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		
		
		
		return ResponseEntity.ok(reserveInfo);
	}
	
	//예약 승인 or 취소 처리
	
	@PostMapping("/reserve/{reserveId}")
	public ResponseEntity<Object> updateStatus(@PathVariable("reserveId") int reserveId, @RequestParam("statusCode") int statusCode , HttpSession session){
		
		int result = service.updateStatus(reserveId,statusCode);
		
		return ResponseEntity.ok(result == 1 ? "예약 처리 완료" : "예약 처리 실패");
	}

	// 예약 상태 확인
	@GetMapping("/reserve/status/{reserveId}")
	public ResponseEntity<Object> checkStatus(@PathVariable("reserveId") int reserveId, HttpSession session){
		ReserveDTO reserve = service.getReserve(reserveId);
		return ResponseEntity.ok(reserve);
	}
	
	//예약 가능 여부 확인 
	@GetMapping("/reserve/available/{storeId}")
	public ResponseEntity<Object> checkAvailableReserve(@PathVariable("storeId") int storeId,@RequestParam("reserveTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime reserveTime, HttpSession session){
		log.info("{}, {}", storeId, reserveTime);
		ReserveSlotsDTO reserveSlot = service.getReserveSlots(storeId, reserveTime);
		return ResponseEntity.ok(reserveSlot);
	}
	
	//유저 알림 조회 (이건 유저 컨트롤단에서 추가해주면 될듯)
	@GetMapping("/notification")
	public ResponseEntity<Object> getNotification(@RequestParam("userId") int userId){
		List<NotificationDTO> notification = service.getNotification(userId);
		return ResponseEntity.ok(notification);
	}
	
	@PutMapping("/notification")
	public ResponseEntity<Object> readNotification(@RequestParam("alarmId") int alarmId){
		int result = service.updateNotification(alarmId);
		return ResponseEntity.ok(result == 1 ? "읽음 처리 완료" : "읽음 처리 실패");
	}
	
}
