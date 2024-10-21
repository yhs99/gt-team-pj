package com.team.goott.user.cart.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.cart.persistence.UserCartDAO;
import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.MenuDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCartServiceImpl implements UserCartService {
	
	@Inject
	private UserCartDAO cartDAO;
	 
	@Override
	public List<CartDTO> getUserCart(int userId) throws Exception {
		log.info(userId +"");
		return cartDAO.getUserCart(userId);
	}

	@Override
	public void addCart(CartDTO cartDTO) throws Exception {
	    List<MenuDTO> menuList = cartDAO.getMenuCart(cartDTO.getMenuId());
	    
	    
	    if (menuList.isEmpty()) {
	        throw new Exception("해당 메뉴가 존재하지 않습니다.");
	    }

	    MenuDTO menu = menuList.get(0);

	    cartDTO.setMenuId(menu.getMenuId()); 
	    cartDTO.setPrice(menu.getPrice());   
	    cartDTO.setMenuName(menu.getMenuName()); 

	    double totalPrice = cartDTO.getPrice() * cartDTO.getStock();
	    cartDTO.setTotalPrice(totalPrice);

	    cartDAO.addCart(cartDTO);
	}

	@Override
	public void deleteFromCart(int cartId, int userId) throws Exception {
		cartDAO.deleteFromCart(cartId, userId);
	}

	  
	
   
    
    
}
