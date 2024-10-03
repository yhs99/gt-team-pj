package com.team.goott.home.controller;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository
public class TestMapperImpl implements TestMapper{

	private final static String NS = "com.team.mappers.test.";

	@Inject
	private SqlSession ses;
	
	@Override
	public List<TestVO> getTestData() {
		return ses.selectList(NS+"selectTable");
	}
	
	
}
