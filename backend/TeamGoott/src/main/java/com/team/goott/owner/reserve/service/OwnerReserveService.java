package com.team.goott.owner.reserve.service;

import com.team.goott.owner.domain.ReserveInfoVO;

public interface OwnerReserveService {

	ReserveInfoVO getAllReserveInfo(int storeId, String sortMethod);

	int updateStatus(int reserveId, int statusCode);

}
