package com.team.goott.user.domain;

import java.util.ArrayList;
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
public class ExtendedCartDTO {
	private int cartId;
	private int storeId;
	private int menuId;
	private String menuName;
	private int stock;
	private double price;
	private double totalPrice;
	private int maxPeoplePerReserve;
	private String menuImageUrl;
	private String storeName;
	private List<String> url = new ArrayList<>();
	private List<CouponVO> availableCoupons = new ArrayList<>();
}
