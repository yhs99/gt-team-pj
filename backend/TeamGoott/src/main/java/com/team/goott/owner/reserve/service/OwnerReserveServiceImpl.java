package com.team.goott.owner.reserve.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.ReserveInfoVO;
import com.team.goott.owner.reserve.persistence.OwnerReserveDAO;
import com.team.goott.user.domain.ReserveDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerReserveServiceImpl implements OwnerReserveService {
	
	@Inject
	OwnerReserveDAO reserveDAO;
	
	
	@Override
	public ReserveInfoVO getAllReserveInfo(int storeId, String sortMethod) {
		List<ReserveDTO> reserveList = reserveDAO.getAllReserve(storeId, sortMethod);
		int totalReserve = reserveDAO.getTotalReserve(storeId);
		int totalTodayReserve = reserveDAO.getTotalTodayReserve(storeId);
		
		 return ReserveInfoVO.builder().totalReserve(totalReserve).totalTodayReserve(totalTodayReserve).reservations(reserveList).build();
	}

}
