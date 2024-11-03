package com.team.goott.owner.sales.persistence;

import java.util.List;

import com.team.goott.owner.domain.SalesInfoVO;

public interface OwnerSalesDAO {

	List<SalesInfoVO> getSales(int storeId);

}
