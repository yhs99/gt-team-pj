package com.team.goott.admin.reserve.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.admin.domain.ReservesVO;
import com.team.goott.admin.reserve.persistence.AdminReserveDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminReserveServiceImpl implements AdminReserveService {
	
	@Autowired
	private AdminReserveDAO adminReserveDAO;
	
	@Override
	public List<ReservesVO> getReserveLists(Map<String, Object> filters) {
		List<ReservesVO> ls = adminReserveDAO.getReserveLists(filters);
		ls.stream()
		.forEach(ReservesVO::setPrices);
		return ls;
	}

}
