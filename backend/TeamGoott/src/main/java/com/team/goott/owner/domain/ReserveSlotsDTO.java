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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ReserveSlotsDTO {
	private int reserveSlotId;
	private int storeId;
	private LocalDateTime slotDatetime;
	private boolean reserved;
	private int userId;
}
