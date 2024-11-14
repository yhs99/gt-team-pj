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
public class BookmarkDTO {
	private int bookMarkId;
	private int userId;
	private int storeId;

public BookmarkDTO(int userId, int storeId) {
	
	if (userId <= 0) {
        throw new IllegalArgumentException("유효한 userId를 입력해야 합니다.");
    }
	
	this.userId =userId;
	this.storeId = storeId;
}

}