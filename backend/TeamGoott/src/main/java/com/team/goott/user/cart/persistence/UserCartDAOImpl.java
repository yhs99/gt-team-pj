package com.team.goott.user.cart.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.CartDTO;
import com.team.goott.user.domain.ExtendedCartDTO;
import com.team.goott.user.domain.MenuDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserCartDAOImpl implements UserCartDAO {
	
	@Inject
	private SqlSession ses;
	
	private static String ns = "com.team.mappers.user.cart.userCartMapper.";
	
	@Override
	public List<CartDTO> getUserCart(int userId) throws Exception {
		return ses.selectList(ns+"getAllCart",userId);
	}

	@Override
	public int addCart(CartDTO cartDTO) throws Exception {
		return ses.insert(ns+"addToCart",cartDTO);
	}

	@Override
	public int deleteFromCart(int cartId, int userId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("cartId", cartId);
		params.put("userId", userId);
		return ses.delete(ns + "deleteFromCart", params);
	}

	@Override
	public List<MenuDTO> getMenuCart(int menuId) throws Exception {
		return ses.selectList(ns+"getMenuCart",menuId);
	}

	@Override
	public List<ExtendedCartDTO> getUserCartById(int userId) throws Exception {
		return ses.selectList(ns+"getUserCartById", userId);
	}

	@Override
	public List<Integer> getCartStoreList(int userId) throws Exception {
		return ses.selectList(ns+"getCartStoreList", userId);
	}


}
