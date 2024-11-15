package com.team.goott.owner.sales.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.SalesByDateVO;
import com.team.goott.owner.domain.SalesVO;

@Repository
public class OwnerSalesDAOImpl implements OwnerSalesDAO {
	
	@Inject
	SqlSession session;
	private static String ns = "com.team.mappers.owner.sales.ownerSalesMapper.";


	@Override
	public List<SalesVO> getSales(int storeId) {
		return session.selectList(ns+"getSalesList", storeId);
	}


	@Override
	public int getTotalSales(int storeId) {
		return session.selectOne(ns+"getTotalSales", storeId);
	}


	@Override
	public int getTotalSalesCount(int storeId) {
		return session.selectOne(ns+"getTotalSalesCount", storeId);
	}


	@Override
	public int getTodayTotalSales(int storeId) {
		return session.selectOne(ns+"getTodayTotalSales", storeId);
	}


	@Override
	public int getTodayTotalSalesCount(int storeId) {
		return session.selectOne(ns+"getTodayTotalSalesCount", storeId);
	}


	@Override
	public List<SalesByDateVO> getSalesByDate(int storeId) {
		return session.selectList(ns+"getSalesByDate", storeId);
	}
	
	

}
