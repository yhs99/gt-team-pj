package com.team.goott.owner.domain;

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
public class StoreCategoryDTO {
	private int storeId;
	private String categoryCodeId; 
	private String storeCategoryName;
}
