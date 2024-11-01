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
public class ReviewInfoVO {
	private int totalReview;
	private int todayReview;
	private float totalScore;
	private float todayTotalScore;
	private int[] countScore;
	private int[] countMonthlyReview;
	private List<ReviewVO> reviews;
}
