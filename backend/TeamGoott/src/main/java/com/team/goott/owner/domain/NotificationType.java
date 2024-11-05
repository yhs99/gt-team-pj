package com.team.goott.owner.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum NotificationType {
	OWNER_TO_CUSTOMER,
	CUSTOMER_TO_OWNER;
	
}
