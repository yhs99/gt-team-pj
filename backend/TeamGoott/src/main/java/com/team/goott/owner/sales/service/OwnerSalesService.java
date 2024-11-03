package com.team.goott.owner.sales.service;

import java.util.List;

import com.team.goott.owner.domain.SalesInfoVO;

public interface OwnerSalesService {

	List<SalesInfoVO> getSales(int storeId);

}
