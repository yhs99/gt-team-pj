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
public class SalesVO {
	private int payHistoryId;
	private int reserveId;
	private int storeId;
	private int menuId;
	private String menuName;
	private int stock;
	private double stockPerPrice;
	private double totalPrice;
	private boolean couponYN;
	private int couponId;
	private double payAmount;
	private Timestamp reserveTime;
}
