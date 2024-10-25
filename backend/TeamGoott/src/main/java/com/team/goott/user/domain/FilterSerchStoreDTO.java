package com.team.goott.user.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

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
public class FilterSerchStoreDTO {
	private int storeId;
	private int ownerId;
	private int rotationId;
	private int sidoCodeId;
	private String address;
	private String tel;
	private String description;
	private String directionGuide;
	private int maxPeople;
	private int maxPeoplePerReserve;
	private BigDecimal locationLatX;
	private BigDecimal locationLonY;
	private int menuId;
	private String menuName;
	private int price;
	private String menuImageUrl;
	private String menuImageName;
	private boolean isMain;	private String categoryCodeId;
	private int categoryId;
	private String storeCategoryName;
	private String couponName;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime start;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime end;
	private int discount;
	private int stock;
	private String sidoName;
	private int scheduleId;
	private int dayCodeId;
	private LocalTime open;
	private LocalTime close;
	private boolean closeDay;

}
