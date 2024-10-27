package com.team.goott.admin.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class StoreScheduleVO {
	private int scheduleId;
	private int dayCodeId;
	@JsonFormat(pattern = "HH:mm")
	private LocalDateTime open;
	@JsonFormat(pattern = "HH:mm")
	private LocalDateTime close;
	private String dayOfWeek;
	private boolean closeDay;
	
	public void setDayCodeId(int dayCodeId) {
		this.dayCodeId = dayCodeId;
		switch (dayCodeId) {
		case 0:
			this.dayOfWeek = "일요일";
			break;
		case 1:
			this.dayOfWeek = "월요일";
			break;
		case 2:
			this.dayOfWeek = "화요일";
			break;
		case 3:
			this.dayOfWeek = "수요일";
			break;
		case 4:
			this.dayOfWeek = "목요일";
			break;
		case 5:
			this.dayOfWeek = "금요일";
			break;
		case 6:
			this.dayOfWeek = "토요일";
			break;
		default:
			this.dayOfWeek = "";
			break;
		}
	}
}
