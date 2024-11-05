package com.team.goott.owner.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SalesInfoVO {
	
	private int totalSales; // 총 결제 금액
	private int totalSalesCount; // 총 결제 수
	private int todayTotalSales; // 오늘 자 결제 금액
	private int todayTotalSalesCount; // 오늘 자 결제 수 
	private int[] countMonthlySales; // 최근 6개월 결제 금액
	private int[] countMonthlySalesCount; // 최근 6개월 결제 수
	private List<SalesVO> sales;
}
