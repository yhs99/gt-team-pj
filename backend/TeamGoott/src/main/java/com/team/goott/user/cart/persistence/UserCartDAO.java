package com.team.goott.user.cart.persistence;

import java.util.List;

import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.domain.CartDTO;

public interface UserCartDAO {
	//장바구니 목록 조회
	List<CartDTO> getUserCart(int userId) throws Exception;

	int addCart(CartDTO cartDTO) throws Exception;

	int deleteFromCart(int cartId, int userId) throws Exception;

	List<MenuDTO> getMenuCart(int menuId);


}
