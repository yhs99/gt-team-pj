package com.team.goott.user.cart.service;

import java.util.List;
import com.team.goott.user.domain.CartDTO;

public interface UserCartService {
	List<CartDTO> getUserCart(int userId) throws Exception;

	void addCart(CartDTO cartDTO) throws Exception;

	void deleteFromCart(int cartId, int userId) throws Exception;
}
