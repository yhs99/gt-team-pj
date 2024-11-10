package com.team.goott.owner.sales.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.SalesByDateVO;
import com.team.goott.owner.domain.SalesInfoVO;
import com.team.goott.owner.domain.SalesVO;
import com.team.goott.owner.sales.persistence.OwnerSalesDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OwnerSalesServiceImpl implements OwnerSalesService {
	
	@Inject
	OwnerSalesDAO dao;
	
	@Override
	public SalesInfoVO getSales(int storeId) {
		int totalSales = dao.getTotalSales(storeId);
		int totalSalesCount = dao.getTotalSalesCount(storeId);
		int todayTotalSales = dao.getTodayTotalSales(storeId);
		int todayTotalSalesCount = dao.getTodayTotalSalesCount(storeId);
		List<SalesVO> sales = dao.getSales(storeId);
		List<SalesByDateVO> salesByDate = dao.getSalesByDate(storeId);
		int[] countMonthlySales = new int[6]; // 최근 6개월 결제 금액
		int[] countMonthlySalesCount = new int[6]; // 최근 6개월 결제 수
		
		for(SalesVO sale : sales) {
			//해당 결제내역의 timestamp
			Timestamp saleTimestamp = Timestamp.valueOf(sale.getReserveTime().toString());
			//timestamp -> LocalDateTime으로 변환
			LocalDateTime saleTime = saleTimestamp.toLocalDateTime();
			
			//현재 날짜
			LocalDateTime today = LocalDateTime.now();
			//현재날짜 기준 6개월 전 날짜
			LocalDateTime sixMonthsAgo =  today.minusMonths(6);
			
			if(sixMonthsAgo.isBefore(saleTime) || sixMonthsAgo.isEqual(saleTime)) {
				
				int saleMonth = saleTime.getMonthValue();
				int currentMonth = today.getMonthValue();
				
				int index = (saleMonth - currentMonth + 12) % 12;
				log.info("{}, {}", saleTime, index);
				if(index >= 6) {
					index = 12 - index;
				}
				
				if(index >= 0 ) {
					countMonthlySales[index] += sale.getPayAmount();
					countMonthlySalesCount[index]++;
				}
			}
			
		}
		SalesInfoVO salesInfo = SalesInfoVO.builder().
				totalSales(totalSales).
				totalSalesCount(totalSalesCount).
				todayTotalSales(todayTotalSales).
				todayTotalSalesCount(todayTotalSalesCount).
				countMonthlySales(countMonthlySales).
				countMonthlySalesCount(countMonthlySalesCount).
				salesByDate(salesByDate).
				sales(sales).build();
		
		return salesInfo;
	}

}
