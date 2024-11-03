package com.team.goott.owner.sales.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.SalesInfoVO;
import com.team.goott.owner.sales.persistence.OwnerSalesDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OwnerSalesServiceImpl implements OwnerSalesService {
	
	@Inject
	OwnerSalesDAO dao;
	
	@Override
	public List<SalesInfoVO> getSales(int storeId) {
		return dao.getSales(storeId);
	}

}
