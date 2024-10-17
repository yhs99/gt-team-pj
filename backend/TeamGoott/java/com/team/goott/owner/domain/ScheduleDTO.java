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
public class ScheduleDTO {
	private int scheduleId;
	private int storeId;
	private int dayCodeId;
	private LocalDateTime open;
	private LocalDateTime close;
}
