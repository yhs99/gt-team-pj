package com.team.goott.owner.domain;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Data
public class ScheduleDTO {
	private int storeId;
	private int dayCodeId;
	private LocalTime open;
	private LocalTime close;
	private boolean closeDay;
}
