package com.team.goott.user.domain;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookmarkInfoDTO {
	
	BookmarkDTO bookmarkDto;
	String url;
	String storeName;
	String description;
	float reviewScore;
	int reviewCount;
	String categoryName;
	String sidoName;
	private LocalTime open;
	private LocalTime close;
	private int dayCodeId;
	private boolean closeDay;
	int averagePrice;

}
