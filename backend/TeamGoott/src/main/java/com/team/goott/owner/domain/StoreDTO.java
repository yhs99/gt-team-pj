package com.team.goott.owner.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class StoreDTO {
	private int storeId;
	private int ownerId;
	private int rotationId;
	private String sidoCode;
	private int sidoCodeId;
	private String storeName;
	private String address;
	private String tel;
	private String description;
	private String directionGuide;
	private int maxPeople;
	private int maxPeoplePerReserve;
	private int isBlocked;	
	private BigDecimal locationLatX;
	private BigDecimal locationLonY;
}
