package com.team.goott.owner.domain;

import java.time.LocalDateTime;

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
public class NotificationDTO {
	
	private int alarmId;
	private int userId;
	private String message;
	private boolean isRead;
	private LocalDateTime createAt;
	private int storeId;
	private NotificationType notificationType;
	private int reserveId;
}
