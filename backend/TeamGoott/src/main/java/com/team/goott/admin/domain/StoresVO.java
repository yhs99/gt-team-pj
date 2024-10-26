package com.team.goott.admin.domain;
import java.math.BigDecimal;
import java.util.List;

import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;

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
public class StoresVO {
	private int storeId;
	private int ownerId;
	private int rotationId;
	private int sidoCodeId;
	private String storeName;
	private String address;
	private String tel;
	private String description;
	private String directionGuide;
	private int maxPeople;
	private int maxPeoplePerReserve;
	private BigDecimal locationLatX;
	private BigDecimal locationLonY;
	private List<StoreImagesVO> storeImages;
	private List<FacilityDTO> facilities;
	private List<StoreCategoryVO> storeCategories;
	private List<StoreScheduleVO> storeSchedules;
}
