package com.team.goott.owner.domain;


import java.sql.Timestamp;

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
public class ReviewVO {
	private int reviewId;
	private int storeId;
	private int userId;
	private int score;
	private String content;
	private Timestamp createAt;
	private boolean isDeleteReq;
}
