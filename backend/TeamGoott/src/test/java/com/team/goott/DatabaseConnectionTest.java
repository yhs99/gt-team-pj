package com.team.goott;

import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.team.goott.home.controller.TestMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/*.xml" })
public class DatabaseConnectionTest {

	@Inject
	private DataSource ds;
	@Inject
	private TestMapper mapper;
	
    @Test
	public void testConnection() throws Exception {
		try (Connection con = ds.getConnection()) {
			System.out.println(mapper.getTestData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
