package com.team.goott.user.domain;

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
	private int categoryId; 
	private int storeId;
	private int categoryCodeId;
	private String categoryName;
}
