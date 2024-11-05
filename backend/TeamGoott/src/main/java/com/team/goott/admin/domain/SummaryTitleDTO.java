package com.team.goott.admin.domain;

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
public class SummaryTitleDTO {
	// 모든 매출액은 statusCode가 2, 4, 5인 데이터만 집계한다
	private Double totalSales; //총 매출액
	private Double todaySales; //금일 매출
	private int totalReserveCnt; // 총 예약 건수
	private int todayReserveCnt; // 금일 예약 건수
}
