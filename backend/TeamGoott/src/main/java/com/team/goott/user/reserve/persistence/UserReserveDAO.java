package com.team.goott.user.reserve.persistence;

import java.time.LocalDateTime;
import java.util.List;

import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.ReserveDTO;

public interface UserReserveDAO {

	List<CartDTO> getCartCheck(int userId) throws Exception;

	int insertReserve(int userId, ReserveDTO reserveDTO)throws Exception;

	int getMaxPeople(int storeId)throws Exception;

	int getTimeTotPeople(int storeId, LocalDateTime reserveTime) throws Exception;

	int getMaxPeoplePerReserve(int storeId) throws Exception;

	Object updateReserved(int storeId, LocalDateTime reserveTime) throws Exception;

	List<MenuDTO> getStoreMenuById(int menuId, int storeId);

	int updateReserve(int reserveId, int userId);

}
