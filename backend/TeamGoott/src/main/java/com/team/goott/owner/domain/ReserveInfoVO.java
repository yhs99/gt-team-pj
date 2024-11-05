package com.team.goott.owner.domain;


import java.util.List;

import com.team.goott.user.domain.ReserveDTO;

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
public class ReserveInfoVO {
	private int totalReserve;
	private int totalTodayReserve;
	
	private List<ReserveDTO> reservations; 
}
