package com.team.goott.user.reserve.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.UserOnly;
import com.team.goott.user.reserve.service.UserReserveService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/reserve")
public class UserReserveController {

	@Inject
	private UserReserveService userReserveService;

	// 예약 생성
	@UserOnly
	@PostMapping()
	public ResponseEntity<String> createReserve(HttpSession session, @RequestBody ReserveDTO reserveDTO) {
		 //로그인 확인
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		
		try {
			// 예약 생성 서비스 호출
			userReserveService.createReserve(userSession.getUserId(), reserveDTO);
			return ResponseEntity.ok("예약해주셔서 감사합니다.");
		} catch (IllegalArgumentException e) {
			// 유효성 검증 실패 시
			e.getStackTrace();
			log.warn("예약 생성 중 유효성 오류: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			// 기타 예외 처리
			log.error("예약 생성 중 서버 오류: {}", e.getMessage(), e);
			e.getStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
		}
	}
	
	
	// 에약 제거
	@UserOnly
	@DeleteMapping("/{reserveId}")
	public ResponseEntity<String> removeReserve(@PathVariable int reserveId, HttpSession session){
		// 로그인 확인
		UserDTO userSession = (UserDTO) session.getAttribute("user");
		
		
		try {
			int result = userReserveService.updateReserve(reserveId, userSession.getUserId());
			if(result > 0) {
				return ResponseEntity.ok("예약이 성공적으로 취소되었습니다.");
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원의 예약을 찾을 수 없습니다. 다시 한번 확인해주세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예약 취소 중 오류가 발생했습니다. 다시 시도해 주세요.");
		}
	}
}
