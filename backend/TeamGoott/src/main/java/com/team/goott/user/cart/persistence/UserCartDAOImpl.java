package com.team.goott.user.cart.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.CartDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserCartDAOImpl implements UserCartDAO {
	
	@Inject
	private SqlSession ses;
	private static String ns = "com.team.mappers.user.cart.userCartMapper.";
	
	@Override
	public List<CartDTO> getUserCart(int userId) throws Exception {
		log.info(userId + " ");
		return ses.selectList(ns+"getAllCart",userId);
	}

	@Override
	public int addCart(CartDTO cartDTO) throws Exception {
		return ses.insert(ns+"addToCart",cartDTO);
	}

	@Override
	public int deleteFromCart(int cartId, int userId) throws Exception {
		// 넘겨줘야할 파라메터가 2개 이상이면, Map을 이용하여 파라메터를 매핑하여 넘겨준다
				Map<String, Object> params = new HashMap<>();
				params.put("cartId", cartId);
				params.put("userId", userId);
				return ses.delete(ns + "deleteFromCart", params);
	}

}
