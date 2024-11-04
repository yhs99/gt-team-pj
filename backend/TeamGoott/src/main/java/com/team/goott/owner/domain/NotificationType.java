package com.team.goott.owner.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum NotificationType {
	OWNER_TO_CUSTOMER("owner_to_customer"),
	CUSTOMER_TO_OWNER("customer_to_owner");
	
	private final String val;
}
