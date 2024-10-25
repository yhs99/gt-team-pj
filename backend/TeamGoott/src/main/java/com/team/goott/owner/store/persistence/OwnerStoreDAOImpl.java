package com.team.goott.owner.store.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerStoreDAOImpl implements OwnerStoreDAO {
	
	
	private final static String NS = "com.team.mappers.owner.store.ownerStoreMapper.";

	@Inject
	private SqlSession ses;
	
	
	@Override
	public StoreDTO login(String id, String pw) {
		// 점주 로그인
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("pw", pw);
		return ses.selectOne(NS+"ownerLoginInfo", map);
	}

	@Override
	public boolean ownerRegister(OwnerDTO ownerDTO) {
		// 점주 가입
		Integer sqlResult= ses.insert(NS+"ownerRegister", ownerDTO);
		if (sqlResult.equals(1)) {
			return true;
		}else {
			return false;
		}
		
	}

}
