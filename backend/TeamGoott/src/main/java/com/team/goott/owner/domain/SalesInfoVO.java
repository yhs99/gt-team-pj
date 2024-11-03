package com.team.goott.owner.domain;

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
}
