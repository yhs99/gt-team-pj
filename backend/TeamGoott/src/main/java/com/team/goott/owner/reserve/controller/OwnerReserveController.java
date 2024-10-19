package com.team.goott.owner.reserve.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.ReserveInfoVO;
import com.team.goott.owner.reserve.service.OwnerReserveService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/owner")
public class OwnerReserveController {
	
	@Inject
	OwnerReserveService service;
	
	@GetMapping("/reserve")
	public ResponseEntity<Object> getAllReservation(@RequestParam(value = "sortMethod", defaultValue = "newest") String sortMethod , HttpSession session) {
		log.info("예약목록 API 기능 동작");
		int storeId = 29;
		
		ReserveInfoVO reserveInfo = service.getAllReserveInfo(storeId, sortMethod);
		System.out.println("reserveInfo : " + reserveInfo.toString());
		
		return ResponseEntity.ok(reserveInfo);
	}
}
