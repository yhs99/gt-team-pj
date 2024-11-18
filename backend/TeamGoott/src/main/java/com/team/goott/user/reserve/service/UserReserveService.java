package com.team.goott.user.reserve.service;


import java.util.List;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReserveListsVO;

public interface UserReserveService {

	void createReserve(int userId, ReserveDTO reserveDTO) throws Exception;

	int updateReserve(int reserveId, int userId) throws Exception;

	List<ReserveListsVO> getUserReserveLists(int userId, String reserveType);

}
