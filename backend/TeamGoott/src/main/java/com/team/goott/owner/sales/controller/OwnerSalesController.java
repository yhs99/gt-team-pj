package com.team.goott.owner.sales.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.SalesInfoVO;
import com.team.goott.owner.domain.SalesVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.sales.service.OwnerSalesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/owner")
public class OwnerSalesController {
	
	@Inject
	OwnerSalesService service;
	
	@GetMapping("/sales")
	public ResponseEntity<Object> getSalesInfo(HttpSession session){
		
		SalesInfoVO salesInfo = null;
		StoreDTO storeSession = (StoreDTO) session.getAttribute("store");
		int storeId = storeSession.getStoreId();
//		로그인 시
		if(storeSession != null) {
			salesInfo = service.getSales(storeId);
		} else {
			// 로그인 실패 시 
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");

		}
		return ResponseEntity.ok(salesInfo);
	}
	
}
