package com.team.goott.user.reserve.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.NotificationType;
import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.PayHistoryDTO;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReserveListsVO;
import com.team.goott.user.reserve.persistence.UserReserveDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserReserveServiceImpl implements UserReserveService {

	@Inject
	private UserReserveDAO userReserveDAO;

	@Override
	@Transactional (isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class) // 트랜잭션 적용
	public void createReserve(int userId, ReserveDTO reserveDTO) throws Exception {
		// 카트 조회
		List<CartDTO> cartList = userReserveDAO.getCartCheck(userId); // 사용자 ID로 카트 목록 조회
		// 카트에 메뉴가 담겼는지 확인
		if (cartList.isEmpty()) {
			throw new IllegalArgumentException("카트가 비어 있습니다.");
		}
		int storeId = cartList.get(0).getStoreId();
		reserveDTO.setStoreId(storeId);
	    // 유효성 체크 (예약 인원 수, 메뉴 확인 등)
	    validateReservation(userId, reserveDTO);
	    
	    // 예약 등록
	    userReserveDAO.insertReserve(userId, reserveDTO);

	    double totalAmount = 0;

	    // 총 금액 계산
	    for (CartDTO cart : cartList) {
	        totalAmount += cart.getPrice() * cart.getStock();
	    }

	    double finalAmount;
	    
	    // 쿠폰이 있는 경우와 없는 경우 처리
	    if (reserveDTO.getCouponId() != null && reserveDTO.getCouponId() > 0) {
	        Integer discount = userReserveDAO.getCouponDiscount(reserveDTO.getCouponId());
	        discount = (discount == null) ? 0 : discount;

	        LocalDateTime currentDateTime = LocalDateTime.now();
	        LocalDateTime startTime = userReserveDAO.getCouponStartTime(reserveDTO.getCouponId());
	        LocalDateTime endTime = userReserveDAO.getCouponEndTime(reserveDTO.getCouponId());
	        
	        Integer couponStoreId = userReserveDAO.getCouponStoreId(reserveDTO.getCouponId());
	        if (couponStoreId == null || !couponStoreId.equals(reserveDTO.getStoreId())) {
	            throw new IllegalArgumentException("해당 쿠폰은 선택한 매장에 쿠폰이 아닙니다.");
	        }

	        Integer stock = userReserveDAO.getCouponStockCheck(reserveDTO.getCouponId());
	        if (stock == null || stock <= 0) {
	            throw new IllegalArgumentException("유효하지 않은 쿠폰입니다.");
	        }

	        if ((startTime != null && currentDateTime.isBefore(startTime)) || 
	            (endTime != null && currentDateTime.isAfter(endTime))) {
	            throw new IllegalArgumentException("유효하지 않은 쿠폰입니다. 사용기간을 확인해 주세요.");
	        }

	        // 할인 계산
	        if (discount >= 100) { // 고정할인
	            finalAmount = totalAmount - discount;
	            finalAmount = Math.max(finalAmount, 0);
	        } else { // 퍼센트 할인
	            finalAmount = totalAmount - (totalAmount * discount * 0.01);
	        }

	        // 쿠폰 사용 처리
	        userReserveDAO.updateCouponStock(reserveDTO.getCouponId());
	    } else {
	        // 쿠폰이 없는 경우 기본 금액으로 결제
	        finalAmount = totalAmount;
	    }
	    
	    for (CartDTO cart : cartList) {
	        PayHistoryDTO payHistoryDTO = new PayHistoryDTO();
	        payHistoryDTO.setReserveId(reserveDTO.getReserveId());
	        payHistoryDTO.setStoreId(cart.getStoreId());
	        payHistoryDTO.setMenuId(cart.getMenuId());
	        payHistoryDTO.setMenuName(cart.getMenuName());
	        payHistoryDTO.setStock(cart.getStock());
	        payHistoryDTO.setStockPerPrice(cart.getPrice());
	        payHistoryDTO.setTotalPrice(cart.getPrice() * cart.getStock());

	        payHistoryDTO.setCouponId(reserveDTO.getCouponId() != null ? reserveDTO.getCouponId() : 0);
	        payHistoryDTO.setCouponYN(reserveDTO.getCouponId() != null ? 1 : 0);
	        payHistoryDTO.setPayAmount(finalAmount);

	        userReserveDAO.insertPayHistory(payHistoryDTO);
	    }
			
	    // 결제 성공 후 카트 비우기
	    userReserveDAO.deleteCart(userId);

	    if (getTimeTotPeople(reserveDTO.getStoreId(), reserveDTO.getReserveTime()) == getMaxPeople(reserveDTO.getStoreId())) {
	        updateReserved(reserveDTO.getStoreId(), reserveDTO.getReserveTime());
	    }
	}

	// 예약 인원 확인
	private void validateReservation(int userId, ReserveDTO reserveDTO) throws Exception {
		List<CartDTO> cartList = getCartCheck(userId);
		int storeId = cartList.get(0).getStoreId();
		reserveDTO.setStoreId(storeId);
		if (reserveDTO.getPeople() + getTimeTotPeople(reserveDTO.getStoreId(), reserveDTO.getReserveTime()) > getMaxPeople(reserveDTO.getStoreId())) {
			throw new IllegalArgumentException("예약 인원이 초과합니다.");
			// 예약할 때 기본인원을 1명으로 해서 0명이 나오지 않게
		}
		if (reserveDTO.getPeople() > getMaxPeoplePerReserve(reserveDTO.getStoreId())) {
			throw new IllegalArgumentException("예약 인원이 한 예약당 최대 인원을 초과합니다.");
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
		if(userReserveDAO.getSlotCheck(reserveDTO.getStoreId(), reserveDTO.getReserveTime()) == null) {
			throw new IllegalArgumentException("현재 예약하려는 시간이 존재하지 않습니다 다시 확인해주세요.");
		}
		if (Integer.valueOf(1).equals(userReserveDAO.getSlotCheck(reserveDTO.getStoreId(), reserveDTO.getReserveTime()))) {
		    throw new IllegalArgumentException("예약이 닫혀있습니다. 식당에 문의해주세요.");
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
	public int updateReserve(int reserveId, int userId) throws Exception {
		Timestamp timestamp = userReserveDAO.getReserveTime(reserveId, userId);
		LocalDateTime reserveTime = (timestamp != null) ? timestamp.toLocalDateTime() : null;
		
		int storeId = userReserveDAO.getStoreId(reserveId, userId);
		// 예약이 제거되고 자리가 남으면 reserved를 다시 0으로 변경
		if(getTimeTotPeople(storeId, reserveTime) == getMaxPeople(storeId)) {
			Integer reservedStatus = userReserveDAO.getUpdateReserveSlotReserved(reserveTime, storeId);
			if (reservedStatus != null && reservedStatus == 1) {
				log.info("Slot에 reserved가 0으로 성공적으로 변경되었습니다.");
			}
		}
		return userReserveDAO.updateReserve(reserveId, userId);
	}
  
	@Override
	public List<ReserveListsVO> getUserReserveLists(int userId, String reserveType) {
		return userReserveDAO.getUserReserveLists(userId, reserveType);
  }
  
	//알림 설정
	private int setNotificationToOwner(int userId, ReserveDTO reserveDTO) {
		ReserveDTO reserve = userReserveDAO.getReserve(reserveDTO.getStoreId(), userId, reserveDTO.getReserveTime());
		NotificationDTO notification = new NotificationDTO();
		notification.setUserId(userId);
		notification.setStoreId(reserveDTO.getStoreId());
		notification.setReserveId(reserve.getReserveId());
		notification.setNotificationType(NotificationType.valueOf("CUSTOMER_TO_OWNER"));
		notification.setMessage("신규 예약이 등록 되었습니다. 예약번호 : " + reserve.getReserveId());
		
		return userReserveDAO.setNotification(notification);
	}
}
