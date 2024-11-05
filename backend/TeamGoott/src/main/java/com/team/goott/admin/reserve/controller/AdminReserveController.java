package com.team.goott.admin.reserve.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.admin.domain.AdminOnly;
import com.team.goott.admin.reserve.persistence.AdminReserveDAO;
import com.team.goott.admin.reserve.service.AdminReserveService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminReserveController {

	@Autowired
	private AdminReserveService adminReserveService;
	@Autowired
	private AdminReserveDAO adminReserveDAO;
	
	@AdminOnly
	@GetMapping("/reserve")
	public ResponseEntity<Object> getReserveLists(HttpSession session
												, @RequestParam(name = "storeName", required = false) String storeName
												, @RequestParam(name = "userName", required = false) String userName
												, @RequestParam(name = "statusCodeId", required = false) List<Integer> statusCodeId) {
		Map<String, Object> filters = new HashMap<String, Object>();
		if(storeName != null) filters.put("storeName", storeName);
		if(userName != null) filters.put("userName", userName);
		if(statusCodeId != null) filters.put("statusCodeId", statusCodeId);
		return ResponseEntity.ok(adminReserveService.getReserveLists(filters));
	}

	@GetMapping("/reserveStatusFilters")
	public ResponseEntity<Object> getReserveStatusFilters() {
		return ResponseEntity.ok(adminReserveDAO.getReserveStatusCodeFilters());
	}
	
}
