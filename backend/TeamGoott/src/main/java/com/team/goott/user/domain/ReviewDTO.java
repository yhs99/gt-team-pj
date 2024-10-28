package com.team.goott.user.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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
	private int reserveId;
	private int score;
	private String content;
	private Timestamp createAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createAtLocalDateTime;
	private boolean isDeleteReq;
	
	private List<MultipartFile> inputImages; 
	private List<ReviewImagesDTO> reviewImages;
	
	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
		setCreateAtLocalDateTime(createAt);
	}
	
	public void setCreateAtLocalDateTime(Timestamp createAt) {
		this.createAtLocalDateTime = createAt.toLocalDateTime();
	}
}
