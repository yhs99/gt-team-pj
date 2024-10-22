package com.team.goott.user.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ReserveDTO {
	private int reserveId;
	private int storeId;
	private int userId;
	private int couponId;
	private LocalDateTime reserveTime;
	private String name;
	private int people;
	private int statusCodeId;
	private String memo;
	private int charge;
}
