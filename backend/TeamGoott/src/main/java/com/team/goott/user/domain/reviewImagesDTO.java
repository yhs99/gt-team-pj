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
public class reviewImagesDTO {
	private int imageId;
	private int reviewId;
	private String url;
	private String fileName;
	private String fileType;
	
	private reviewImagesStatus fileStatus;
}
