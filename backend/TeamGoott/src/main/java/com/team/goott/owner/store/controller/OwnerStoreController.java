package com.team.goott.owner.store.controller;

import java.util.Map;

import javax.inject.Inject;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.store.service.OwnerStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OwnerStoreController {
	
	@Inject
	private OwnerStoreService ownerService;
	
	// 점주 가입
	@PostMapping("/owner/register")
	public ResponseEntity<Object> userLoginRequest(OwnerDTO ownerDTO){
		boolean result = false;
		try {
			result = ownerService.register(ownerDTO);
		} catch (DuplicateKeyException | MyBatisSystemException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디 입니다 ");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생 ");
		}
		return ResponseEntity.ok(result?"성공 ":"실패 ");
	}
	
	

}
