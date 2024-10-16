package com.team.goott.user.cart.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.cart.service.UserCartService;
import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserCartController {

	private final UserCartService cartService;

    @Autowired
    public UserCartController(UserCartService cartService) {
        this.cartService = cartService;
    }


		//장바구니 조회
		@GetMapping("/cart")
		public ResponseEntity<Object> getUserCart(HttpSession session) {
		List<CartDTO> cart = null;
		UserDTO userSession = (UserDTO) session.getAttribute("user");
//		if(userSession == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
//		}
		
		try {
			cart = cartService.getUserCart(1);
			  if (cart == null || cart.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("장바구니에 아이템이 없습니다.");
		        }
		} catch (Exception e) {
			log.error("Error retrieving cart for userId {}: {}", userSession.getUserId(), e.getMessage());
            return ResponseEntity.status(500).build();
		}
		return ResponseEntity.ok(cart);
	}
	
	//장바구니에 담기
	@PostMapping("/cart")
	public ResponseEntity<Object> addCart(@RequestBody CartDTO cartDTO ,HttpSession session){
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		
//		if(userSession == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
//		}
		try {
			cartDTO.setUserId(userSession.getUserId());
			cartService.addCart(cartDTO);
			log.info("Item added to cart: {}", cartDTO);
			return ResponseEntity.ok("Item added to cart successfully.");
		} catch (Exception e) {
			log.error("Error adding item to cart: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to add item to cart.");
		}
	}
	
	// 장바구니에 메뉴 삭제
	@DeleteMapping("/cart/{cartId}")
	public ResponseEntity<Object> deleteCartItem(@PathVariable int cartId, HttpSession session){		

		 UserDTO userSession = (UserDTO) session.getAttribute("user");
		    
//		    if (userSession == null) {
//		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
//		    }
		    
		try {
			cartService.deleteFromCart(cartId, userSession.getUserId());
			log.info("Item deleted from cart with cartId: {} for userId: {}", cartId, userSession.getUserId());
	        return ResponseEntity.ok("Item deleted from cart successfully.");
		} catch (Exception e) {
			log.error("Error deleting item from cart: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to delete item from cart.");
		}
	}
}
