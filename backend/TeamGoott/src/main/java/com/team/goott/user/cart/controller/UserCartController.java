package com.team.goott.user.cart.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

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
import com.team.goott.user.domain.ExtendedCartDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.UserOnly;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserCartController {

	@Inject
	UserCartService userCartService;

	// 장바구니 조회
	@UserOnly
	@GetMapping("/cart")
	public ResponseEntity<Object> getUserCartById(HttpSession session) {
		List<ExtendedCartDTO> cart = null;
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		
		try {
			cart = userCartService.getUserCartById(userSession.getUserId());
			if (cart == null || cart.isEmpty()) {
				return ResponseEntity.ok("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니를 불러오는데 실패했습니다");
		}
		return ResponseEntity.ok(cart);
	}

	// 장바구니에 담기
	@UserOnly
	@PostMapping("/cart")
	public ResponseEntity<Object> addCart(@RequestBody CartDTO cartDTO, HttpSession session) {
		UserDTO userSession = (UserDTO) session.getAttribute("user");

		try {
			userCartService.addCart(cartDTO,userSession.getUserId());
			log.info("메뉴가 추가됐습니다 : {}", cartDTO);
			return ResponseEntity.ok("메뉴가 장바구니에 담겼습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("메뉴 추가에 실패했습니다 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 장바구니에 메뉴 삭제
	@UserOnly
	@DeleteMapping("/cart/{cartId}")
	public ResponseEntity<Object> deleteCartItem(@PathVariable int cartId, HttpSession session) {

		UserDTO userSession = (UserDTO) session.getAttribute("user");

		
		try {
			List<CartDTO> cartList = userCartService.getUserCart(userSession.getUserId());
			if (cartList == null || cartList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 장바구니 항목을 찾을 수 없습니다.");
			}
			CartDTO cartItem = cartList.stream().filter(cart -> cart.getCartId() == cartId).findFirst().orElse(null);

			if (cartItem.getUserId() != userSession.getUserId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
			}

			userCartService.deleteFromCart(cartId, userSession.getUserId());

			log.info("메뉴 삭제에 성공했습니다 : {} for userId: {}", cartId, userSession.getUserId());
			return ResponseEntity.ok("삭제가 완료됐습니다. ");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("메뉴 삭제에 실패했습니다 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제에 실패하였습니다.. 다시 한번 확인해주세요!");
		}
	}

}
