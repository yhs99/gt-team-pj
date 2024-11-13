package com.team.goott.user.reserve.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.PayHistoryDTO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReserveListsVO;

public interface UserReserveDAO {

	List<CartDTO> getCartCheck(int userId) throws Exception;

	int insertReserve(int userId, ReserveDTO reserveDTO)throws Exception;

	int getMaxPeople(int storeId)throws Exception;

	int getTimeTotPeople(int storeId, LocalDateTime reserveTime) throws Exception;

	int getMaxPeoplePerReserve(int storeId) throws Exception;

	Object updateReserved(int storeId, LocalDateTime reserveTime) throws Exception;

	List<MenuDTO> getStoreMenuById(int menuId, int storeId) throws Exception;

	int updateReserve(int reserveId, int userId) throws Exception;

	int deleteCart(int userId) throws Exception;

	int getCouponDiscount(int couponId) throws Exception;

	int updateCouponStock(int couponId) throws Exception;

	int insertPayHistory(PayHistoryDTO payHistoryDTO) throws Exception;

	Integer getSlotCheck(int storeId, LocalDateTime reserveTime) throws Exception;

	int getCouponStockCheck(int couponId)throws Exception;

	LocalDateTime getCouponStartTime(int couponId) throws Exception;

	LocalDateTime getCouponEndTime(int couponId) throws Exception;

	Timestamp getReserveTime(int reserveId, int userId) throws Exception;

	int getStoreId(int reserveId, int userId) throws Exception;

	int getUpdateReserveSlotReserved(LocalDateTime reserveTime, int storeId) throws Exception;

	Integer getCouponStoreId(Integer couponId) throws Exception;

	List<ReserveListsVO> getUserReserveLists(int userId, String reserveType);
}
