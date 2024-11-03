package com.team.goott.owner.sales.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.SalesInfoVO;

@Repository
public class OwnerSalesDAOImpl implements OwnerSalesDAO {
	
	@Inject
	SqlSession session;
	private static String ns = "com.team.mappers.owner.reserve.ownerSalesMapper.";


	@Override
	public List<SalesInfoVO> getSales(int storeId) {
		return session.selectList(ns+"getSalesList", storeId);
	}
	
	

}
