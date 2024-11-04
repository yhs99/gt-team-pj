package com.team.goott.user.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Slf4j
public class TodayInfoDTO {
	
	int storeId;
	private int dayOfWeek;
	
	public TodayInfoDTO(int storeId) {
        this.storeId = storeId;
        DayOfWeek day = LocalDate.now().getDayOfWeek(); 

        if (day.getValue() == 7) {//일요일
        	dayOfWeek = 0;
        }else {//월요일
        	dayOfWeek = day.getValue();
        }
        
        log.info("오늘의 요일 :{}", dayOfWeek);
	}

}
