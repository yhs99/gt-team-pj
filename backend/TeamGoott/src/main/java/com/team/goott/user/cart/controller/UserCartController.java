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

	// 장바구니 조회
	@GetMapping("/cart")
	public ResponseEntity<Object> getUserCart(HttpSession session) {
		List<CartDTO> cart = null;
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		if (userSession == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
		}

		try {
			cart = cartService.getUserCart(userSession.getUserId());
			if (cart == null || cart.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("장바구니에 아이템이 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("조회에 실패했습니다. {}: {}", userSession.getUserId(), e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니를 불러오는데 실패했습니다");
		}
		return ResponseEntity.ok(cart);
	}

	// 장바구니에 담기
	@PostMapping("/cart")
	public ResponseEntity<Object> addCart(@RequestBody CartDTO cartDTO, HttpSession session) {
		UserDTO userSession = (UserDTO) session.getAttribute("user");

		if (userSession == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
		}
		try {
			cartDTO.setUserId(userSession.getUserId());
			cartService.addCart(cartDTO);
			log.info("메뉴가 추가됐습니다 : {}", cartDTO);
			return ResponseEntity.ok("메뉴가 장바구니에 담겼습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("메뉴 추가에 실패했습니다 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메뉴가 장바구니에 담기지 못했습니다.. 다시 한번 확인해주세요!");
		}
	}

	// 장바구니에 메뉴 삭제
	@DeleteMapping("/cart/{cartId}")
	public ResponseEntity<Object> deleteCartItem(@PathVariable int cartId, HttpSession session) {

		UserDTO userSession = (UserDTO) session.getAttribute("user");

		if (userSession == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
		}
		
		
		try {
			List<CartDTO> cartList = cartService.getUserCart(userSession.getUserId());
			if(cartList == null || cartList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 장바구니 항목을 찾을 수 없습니다.");
			}
			CartDTO cartItem = cartList.stream()
                    .filter(cart -> cart.getCartId() == cartId)
                    .findFirst()
                    .orElse(null);
			
			if (cartList == null || cartList.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 장바구니 항목을 찾을 수 없습니다.");
	        }
			

			if(cartItem.getUserId() != userSession.getUserId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
			}
			
			cartService.deleteFromCart(cartId, userSession.getUserId());
			

			log.info("메뉴 삭제에 성공했습니다 : {} for userId: {}", cartId, userSession.getUserId());
			return ResponseEntity.ok("삭제가 완료됐습니다. ");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("메뉴 삭제에 실패했습니다 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제에 실패하였습니다.. 다시 한번 확인해주세요!");
		}
	}
}
