package com.team.goott.owner.reserve.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.OwnerOnly;
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
	@OwnerOnly
	@GetMapping("/reserve")
	public ResponseEntity<Object> getAllReservation(@RequestParam(value = "sortMethod", defaultValue = "newest") String sortMethod , HttpSession session) {
		ReserveInfoVO reserveInfo = null;
		//세션 객체 저장
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		int storeId = storeSession.getStoreId();
		reserveInfo = service.getAllReserveInfo(storeId, sortMethod);
		
		return ResponseEntity.ok(reserveInfo);
	}
	
	//예약 승인 or 취소 처리
	
	@OwnerOnly
	@PostMapping("/reserve/{reserveId}")
	@Transactional
	public ResponseEntity<Object> updateStatus(@PathVariable("reserveId") int reserveId, @RequestParam("statusCode") int statusCode , HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		
		int result = 0;
		int storeId = storeSession.getStoreId();
		// 승인 할 예약 가져오기
		ReserveDTO reserve = service.getReserve(reserveId);
		//승인 할 에약의 storeId와 로그인 한 점주의 storeId가 같을 시
		if(storeId == reserve.getStoreId()) {
			// 예약 상태 업데이트
			result = service.updateStatus(reserveId,statusCode,storeId);				
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 예약에 대한 권한이 없습니다.");
		}
		return ResponseEntity.ok(result == 1 ? "예약 처리 완료" : "예약 처리 실패");
	}

	// 예약 상태 확인
	@OwnerOnly
	@GetMapping("/reserve/status/{reserveId}")
	public ResponseEntity<Object> checkStatus(@PathVariable("reserveId") int reserveId, HttpSession session){
		ReserveDTO reserve = null;
		reserve = service.getReserve(reserveId);
		
		return ResponseEntity.ok(reserve);
	}
	
	//예약 가능 여부 확인
	@OwnerOnly
	@GetMapping("/reserve/available")
	public ResponseEntity<Object> checkAvailableReserve(@RequestParam("reserveTime") String reserveTime, HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		int storeId = storeSession.getStoreId();
		log.info("{}, {}", storeId, reserveTime);
		LocalDate localDate = null;
		localDate = LocalDate.parse(reserveTime);
		List<ReserveSlotsDTO>  reserveSlot = null;
		reserveSlot = service.getReserveSlots(storeId, localDate);
		return ResponseEntity.ok(reserveSlot);
	}
	
	//점주 알림 조회
	@OwnerOnly
	@GetMapping("/reserve/notification")
	public ResponseEntity<Object> getNotification(HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		List<NotificationDTO> notification = null;
		notification = service.getNotification(storeSession.getStoreId());
		return ResponseEntity.ok(notification);
	}
	
	//알림 읽음 처리
	@OwnerOnly
	@PutMapping("/reserve/notification")
	public ResponseEntity<Object> readNotification(@RequestParam("alarmId") int alarmId, HttpSession session){
		int result = service.updateNotification(alarmId);
		return ResponseEntity.ok(result == 1 ? "읽음 처리 완료" : "읽음 처리 실패");
	}
	
}
