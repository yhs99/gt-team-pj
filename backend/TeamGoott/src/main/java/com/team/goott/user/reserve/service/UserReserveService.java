package com.team.goott.user.reserve.service;


import com.team.goott.user.domain.ReserveDTO;

public interface UserReserveService {

	void createReserve(int userId, ReserveDTO reserveDTO) throws Exception;

	int updateReserve(int reserveId, int userId);



}
