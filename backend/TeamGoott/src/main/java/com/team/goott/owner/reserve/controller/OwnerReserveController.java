package com.team.goott.owner.reserve.controller;

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
		ReserveInfoVO reserveInfo = null;
		//세션 객체 저장
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		
		//로그인시
		if(storeSession != null) {
			int storeId = storeSession.getStoreId();
			reserveInfo = service.getAllReserveInfo(storeId, sortMethod);
			
		} else {
			//로그인 실패 시
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(reserveInfo);
	}
	
	//예약 승인 or 취소 처리
	
	@PostMapping("/reserve/{reserveId}")
	@Transactional
	public ResponseEntity<Object> updateStatus(@PathVariable("reserveId") int reserveId, @RequestParam("statusCode") int statusCode , HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		
		int result = 0;
		if(storeSession != null) {
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
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(result == 1 ? "예약 처리 완료" : "예약 처리 실패");
	}

	// 예약 상태 확인
	@GetMapping("/reserve/status/{reserveId}")
	public ResponseEntity<Object> checkStatus(@PathVariable("reserveId") int reserveId, HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		ReserveDTO reserve = null;
		if(storeSession != null) {
			 reserve = service.getReserve(reserveId);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(reserve);
	}
	
	//예약 가능 여부 확인 
	@GetMapping("/reserve/available/{storeId}")
	public ResponseEntity<Object> checkAvailableReserve(@PathVariable("storeId") int storeId,@RequestParam("reserveTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime reserveTime, HttpSession session){
		log.info("{}, {}", storeId, reserveTime);
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		ReserveSlotsDTO reserveSlot = null;
		if(storeSession != null) {
			reserveSlot = service.getReserveSlots(storeId, reserveTime);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(reserveSlot);
	}
	
	//점주 알림 조회 
	@GetMapping("/reserve/notification")
	public ResponseEntity<Object> getNotification(HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		List<NotificationDTO> notification = null;
		if(storeSession != null) {
			notification = service.getNotification(storeSession.getStoreId());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(notification);
	}
	
	//알림 읽음 처리 
	@PutMapping("/reserve/notification")
	public ResponseEntity<Object> readNotification(@RequestParam("alarmId") int alarmId, HttpSession session){
		StoreDTO storeSession = (StoreDTO)session.getAttribute("store");
		int result = 0;
		if(storeSession != null) {
			result = service.updateNotification(alarmId);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
		}
		return ResponseEntity.ok(result == 1 ? "읽음 처리 완료" : "읽음 처리 실패");
	}
	
}
