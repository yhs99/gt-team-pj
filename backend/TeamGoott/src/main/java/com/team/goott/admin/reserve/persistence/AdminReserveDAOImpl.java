package com.team.goott.admin.reserve.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.admin.domain.ReserveStatusCodeFilter;
import com.team.goott.admin.domain.ReservesVO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AdminReserveDAOImpl implements AdminReserveDAO {
	
	private final static String NS = "com.team.mappers.admin.reserve.adminReserveMapper.";
	
	@Autowired
	private SqlSession ses;
	
	@Override
	public List<ReservesVO> getReserveLists(Map<String, Object> filters) {
		return ses.selectList(NS + "getReserveLists", filters);
	}

	@Override
	public List<ReserveStatusCodeFilter> getReserveStatusCodeFilters() {
		return ses.selectList(NS + "getReserveStatusCodeFilters");
	}

	@Override
	public int updateStatus(Map<String, Object> map) {
		return ses.update(NS + "updateStatus", map);
	}

}
