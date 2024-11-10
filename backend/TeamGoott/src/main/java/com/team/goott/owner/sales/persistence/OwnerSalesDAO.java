package com.team.goott.owner.sales.persistence;

import java.util.List;

import com.team.goott.owner.domain.SalesByDateVO;
import com.team.goott.owner.domain.SalesVO;

public interface OwnerSalesDAO {

	List<SalesVO> getSales(int storeId);

	int getTotalSales(int storeId);

	int getTotalSalesCount(int storeId);

	int getTodayTotalSales(int storeId);

	int getTodayTotalSalesCount(int storeId);

	List<SalesByDateVO> getSalesByDate(int storeId);

}
