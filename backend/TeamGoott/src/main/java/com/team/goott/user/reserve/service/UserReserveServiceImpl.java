package com.team.goott.user.reserve.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.reserve.persistence.UserReserveDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserReserveServiceImpl implements UserReserveService {

	@Inject
	private UserReserveDAO userReserveDAO;

	@Override
	public void createReserve(int userId, ReserveDTO reserveDTO) throws Exception {
		// 유효성 체크 (예약 인원 수, 메뉴 확인 등)
		validateReservation(userId, reserveDTO);

		// 예약 등록

		userReserveDAO.insertReserve(userId, reserveDTO);
		// 예약 등록하고 인원이 가득찼으면 reserved를 1로 변경
		if (reserveDTO.getPeople() + getTimeTotPeople(reserveDTO.getStoreId(),
				reserveDTO.getReserveTime()) == getMaxPeople(reserveDTO.getStoreId())) {
			updateReserved(reserveDTO.getStoreId(), reserveDTO.getReserveTime());
		}
	}

	// 예약 인원 확인
	private void validateReservation(int userId, ReserveDTO reserveDTO) throws Exception {
		List<CartDTO> cartList = getCartCheck(userId);
		if (reserveDTO.getPeople() + getTimeTotPeople(reserveDTO.getStoreId(),
				reserveDTO.getReserveTime()) > getMaxPeople(reserveDTO.getStoreId())) {
			throw new IllegalArgumentException("예약 인원이 초과합니다.");
			// 예약할 때 기본인원을 1명으로 해서 0명이 나오지 않게
		}
		if (reserveDTO.getPeople() > getMaxPeoplePerReserve(reserveDTO.getStoreId())) {
			throw new IllegalArgumentException("예약 인원이 한 예약당 최대 인원을 초과합니다.");
		}
		if (getCartCheck(userId).isEmpty()) {
			throw new IllegalArgumentException("카트가 비어 있습니다.");
		}
		for (CartDTO cart : cartList) {
			// 가게의 메뉴 정보
			List<MenuDTO> storeMenuList = userReserveDAO.getStoreMenuById(cart.getMenuId(), reserveDTO.getStoreId());

			// 메뉴가 존재하지 않거나 가격이 일치하지 않으면 오류
			if (storeMenuList == null || storeMenuList.isEmpty()) {
				throw new IllegalArgumentException("해당 메뉴가 존재하지 않습니다.");
			}
			boolean isValidMenu = false;
			for (MenuDTO storeMenu : storeMenuList) {
				if (cart.getPrice() == storeMenu.getPrice()) {
					isValidMenu = true;
					break; // 일치하는 메뉴를 찾았으니 더 이상 반복할 필요 없음
				}
			}
			if (!isValidMenu) {
				throw new IllegalArgumentException("담긴 메뉴에 오류가 있습니다. 삭제하고 다시 담아주세요.");
			}
		}
	}

	private List<CartDTO> getCartCheck(int userId) throws Exception {
		return userReserveDAO.getCartCheck(userId);
	}

	private void updateReserved(int storeId, LocalDateTime reserveTime) throws Exception {
		userReserveDAO.updateReserved(storeId, reserveTime);
	}

	private int getTimeTotPeople(int storeId, LocalDateTime reserveTime) throws Exception {
		return userReserveDAO.getTimeTotPeople(storeId, reserveTime);
	}

	private int getMaxPeoplePerReserve(int storeId) throws Exception {
		return userReserveDAO.getMaxPeoplePerReserve(storeId);
	}

	private int getMaxPeople(int storeId) throws Exception {
		return userReserveDAO.getMaxPeople(storeId);
	}
	// 예약 제거
	@Override
	public int updateReserve(int reserveId, int userId) {
		return userReserveDAO.updateReserve(reserveId,userId);
	}
}
