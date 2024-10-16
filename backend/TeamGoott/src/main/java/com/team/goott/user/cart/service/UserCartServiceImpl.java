package com.team.goott.user.cart.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.team.goott.user.cart.persistence.UserCartDAO;
import com.team.goott.user.domain.CartDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCartServiceImpl implements UserCartService {
	
	 private UserCartDAO cartDAO;
	 
	@Override
	public List<CartDTO> getUserCart(int userId) throws Exception {
	 return cartDAO.getUserCart(userId);
	}

	@Override
	public void addCart(CartDTO cartDTO) throws Exception {
		
		double totalPrice = cartDTO.getPrice() * cartDTO.getStock();
		cartDTO.setTotalPrice(totalPrice);
		
		cartDAO.addCart(cartDTO);
	}

	@Override
	public void deleteFromCart(int cartId, int userId) throws Exception {
		cartDAO.deleteFromCart(cartId, userId);
	}
	  
	
   
    
    
}
