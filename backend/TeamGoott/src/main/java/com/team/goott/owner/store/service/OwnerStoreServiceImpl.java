package com.team.goott.owner.store.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerStoreServiceImpl implements OwnerStoreService {
	@Inject
	OwnerStoreDAO storeDAO;

	
	@Override
	public StoreDTO login(String id, String pw) {
		// 점주 로그인
		System.out.println("["+id+","+pw+"]");
		StoreDTO storeDTO = storeDAO.login(id, pw);
		return storeDTO;
	}

	@Override
	public boolean register(OwnerDTO ownerDTO) {
		// 점주 가입
		// 레스토랑 정보도 같이 넣게 추가 필요함
		// 2개 테이블 모두 insert 작업이 진행 되다 보니 트렌젝션 필수
		// 수정 진행중
		boolean result = false;

		if (storeDAO.ownerRegister(ownerDTO)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

}
