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
public class MenuDTO {
	private int menuId;
	private int storeId;
	private String menuName;
	private int price;
	private String menuImageUrl;
	private String menuImageName;
	private String description;
	private boolean isMain;
}
