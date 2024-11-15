package com.team.goott.user.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.goott.owner.domain.StoreImagesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReserveListsVO {
	private int reserveId;
	private int storeId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime reserveTime;
	private String name;
	private int people;
	private int statusCodeId;
	private String memo;
	private ReserveDTO reserveInfo;
	private String storeName;
	private String address;
	private String directionGuide;
	private double locationLatX;
	private double locationLonY;
	private List<StoreImagesDTO> storeImages;
	private List<StoreCategoryDTO> categories;
}
