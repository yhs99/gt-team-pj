package com.team.goott.owner.reserve.persistence;

import java.util.List;

import com.team.goott.user.domain.ReserveDTO;

public interface OwnerReserveDAO {

	List<ReserveDTO> getAllReserve(int storeId, String sortMethod);

	int getTotalReserve(int storeId);

	int getTotalTodayReserve(int storeId);

}
