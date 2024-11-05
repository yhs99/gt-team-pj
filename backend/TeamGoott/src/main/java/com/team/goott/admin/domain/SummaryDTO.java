package com.team.goott.admin.domain;

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
public class SummaryDTO {
	private SummaryTitleDTO summaryTitle;
	private List<SummaryVO> dailySales; // 일별 매출
	private List<SummaryVO> monthlySales; // 월간 매출
}
