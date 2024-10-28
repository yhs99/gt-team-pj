package com.team.goott.admin.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.goott.user.domain.ReviewImagesDTO;

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
public class ReviewVO {
	private int reviewId;
	private int storeId;
	private String storeName;
	private int userId;
	private int score;
	private String content;
	private boolean isDeleteReq;
	private Timestamp createAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateTime;
	private String name;
	private String profileImageUrl;
	private List<ReviewImagesDTO> reviewImages;
	private int reviewImagesCount;
}
