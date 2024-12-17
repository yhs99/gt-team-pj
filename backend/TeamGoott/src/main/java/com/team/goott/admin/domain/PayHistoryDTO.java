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
public class PayHistoryDTO {
	private int payHistoryId;
	private int menuId;
	private String menuName;
	private int stock;
	private Double stockPerPrice;
	private Double totalPrice;
	private boolean couponYN;
	private int couponId;
	private int payAmount;
}
