package com.team.goott.owner.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.StoreDTO;


import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.store.service.OwnerStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/store")
public class OwnerStoreController {
	
	@Autowired
	private OwnerStoreService ownerStoreService;
	
	// 점주 가입
	@PostMapping("/owner/register")
	public ResponseEntity<Object> userLoginRequest(OwnerDTO ownerDTO){
		boolean result = false;
		try {
			result = ownerStoreService.register(ownerDTO);
		} catch (DuplicateKeyException | MyBatisSystemException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디 입니다 ");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생 ");
		}
		return ResponseEntity.ok(result?"성공 ":"실패 ");
	}
	
    @PostMapping("")
    public ResponseEntity<Object> registerStore(HttpSession session, StoreDTO store) {
        StoreDTO ownerSession = (StoreDTO) session.getAttribute("ownerId");
//        if (ownerSession == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 로그인 필요
//        }
//        int ownerId = ownerSession.getStoreId();
        int ownerId = 6;
        store.setOwnerId(ownerId);

        // 가게 저장 로직
        try {
            if (ownerStoreService.createStore(store) == 1) {
                return ResponseEntity.ok("가게가 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록에 실패하였습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 등록 중 오류가 발생하였습니다.");
        }
    }
}
