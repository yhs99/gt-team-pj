package com.team.goott.owner.domain;

import java.time.LocalDateTime;

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
public class CouponVO {
	private int couponId;
	private String couponName;
	private int storeId;
	private LocalDateTime start;
	private LocalDateTime end;
	private Integer discount;
	private Integer stock;
	private boolean isDeleted;
}
