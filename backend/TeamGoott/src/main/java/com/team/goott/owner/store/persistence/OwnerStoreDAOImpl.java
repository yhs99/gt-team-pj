package com.team.goott.owner.store.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerStoreDAOImpl implements OwnerStoreDAO {
	
	
	private final static String NS = "com.team.mappers.user.store.userStoreMapper.";

	@Inject
	private SqlSession ses;
	@Override
	public StoreDTO login(String id, String pw) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("pw", pw);
		return ses.selectOne(NS+"ownerLoginInfo", map);
	}

}
