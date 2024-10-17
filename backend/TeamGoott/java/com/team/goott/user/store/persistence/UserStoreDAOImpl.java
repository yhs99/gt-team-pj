package com.team.goott.user.store.persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.StoreDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserStoreDAOImpl implements UserStoreDAO {
	
	@Autowired
	private SqlSession ses;
	
	private static String ns = "com.team.mappers.user.store.userStoreMapper.";
	
	
	@Override
	public List<StoreDTO> getAll() throws Exception {
		return ses.selectList(ns+"getAllStores");
	}

}
