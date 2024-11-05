package com.team.goott.user.domain;

import java.util.List;

import com.team.goott.owner.domain.CategoryCodeVO;
import com.team.goott.owner.domain.FacilityCodeVO;
import com.team.goott.owner.domain.RotationCodeVO;
import com.team.goott.owner.domain.sidoCodeVO;

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
public class StoreFiltersVO {
	private List<CategoryCodeVO> categories;
	private List<FacilityCodeVO> facilities;
	private List<sidoCodeVO> sidoCodes;
	private List<RotationCodeVO> rotations;
}
