package com.team.goott.user.cart.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.user.domain.MenuDTO;
import com.team.goott.user.cart.persistence.UserCartDAO;
import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.ExtendedCartDTO;

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
	public void addCart(CartDTO cartDTO,int userId) throws Exception {
	    List<MenuDTO> menuList = cartDAO.getMenu(cartDTO.getStoreId(), cartDTO.getMenuId());
	    if (menuList.isEmpty()) {
	    	throw new Exception("해당 메뉴가 존재하지 않습니다.");
	    }
	    MenuDTO menu = menuList.get(0);
	    
	    List<Integer> cartStoreList = cartDAO.getCartStoreList(userId);
	    // 카트에 다른 storeId가 이미 존재할 경우 예외 발생
	    if (!cartStoreList.isEmpty() && !cartStoreList.contains(cartDTO.getStoreId())) {
	        throw new Exception("카트에는 한 매장의 상품만 담을 수 있습니다.");
	    }
	    
	    CartDTO existingCart = cartDAO.getCartItem(userId,cartDTO.getMenuId());
	    if(existingCart != null) {
	    	int updatedStock = existingCart.getStock()+cartDTO.getStock();
	    	existingCart.setStock(updatedStock);
	    	existingCart.setTotalPrice(existingCart.getPrice() * updatedStock);
	    	
	    	cartDAO.updateCart(existingCart);
	    }else {
	    	cartDTO.setMenuId(menu.getMenuId()); 
	    	cartDTO.setPrice(menu.getPrice());   
	    	cartDTO.setMenuName(menu.getMenuName()); 
	    	
	    	double totalPrice = cartDTO.getPrice() * cartDTO.getStock();
	    	cartDTO.setTotalPrice(totalPrice);
	    	
	    	cartDAO.addCart(cartDTO,userId);
	    }
	    
	}

	@Override
	public void deleteFromCart(int cartId, int userId) throws Exception {
		cartDAO.deleteFromCart(cartId, userId);
	}

	@Override
	public List<ExtendedCartDTO> getUserCartById(int userId) throws Exception {
		return cartDAO.getUserCartById(userId);
	}
}
