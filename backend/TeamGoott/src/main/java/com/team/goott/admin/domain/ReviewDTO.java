package com.team.goott.admin.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ReviewDTO {
	private int reviewId;
	private int storeId;
	private int userId;
	private int score;
	private String content;
	private Timestamp createAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateTime;
	private boolean isDeleteReq;
	private String name;
	private String profileImageUrl;
	
	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
		setDateTime(createAt);
	}
	
	public void setDateTime(Timestamp createAt) {
		this.dateTime = createAt.toLocalDateTime();
	}
}
