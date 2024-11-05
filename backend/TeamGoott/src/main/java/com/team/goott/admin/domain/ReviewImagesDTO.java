package com.team.goott.admin.domain;

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
public class ReviewImagesDTO {
	private int imageId;
	private int reviewId;
	private String url;
	private String fileName;
	private String fileType;
}
