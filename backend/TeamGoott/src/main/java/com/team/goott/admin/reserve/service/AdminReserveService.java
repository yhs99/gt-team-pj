package com.team.goott.admin.reserve.service;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.ReservesVO;

public interface AdminReserveService {
	List<ReservesVO> getReserveLists(Map<String, Object> filters);
}
