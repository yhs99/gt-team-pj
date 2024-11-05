package com.team.goott.admin.reserve.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.admin.domain.ReserveStatusCodeFilter;
import com.team.goott.admin.domain.ReservesVO;

public interface AdminReserveDAO {
	List<ReservesVO> getReserveLists(Map<String, Object> filters);
	List<ReserveStatusCodeFilter> getReserveStatusCodeFilters();
}
