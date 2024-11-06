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
	public int addCart(CartDTO cartDTO, int userId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		System.out.println(cartDTO.getStoreId());
		params.put("cartDTO", cartDTO);
		params.put("userId", userId);
		return ses.insert(ns+"addToCart",params);
	}

	@Override
	public int deleteFromCart(int cartId, int userId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("cartId", cartId);
		params.put("userId", userId);
		return ses.delete(ns + "deleteFromCart", params);
	}

	@Override
	public List<MenuDTO> getMenuCart(int menuId, int storeId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("menuId", menuId);
		params.put("storeId", storeId);
		System.out.println("paramiter:" + params);
		return ses.selectList(ns+"getMenuCart",params);
	}

	@Override
	public List<ExtendedCartDTO> getUserCartById(int userId) throws Exception {
		return ses.selectList(ns+"getUserCartById", userId);
	}

	@Override
	public List<Integer> getCartStoreList(int userId) throws Exception {
		return ses.selectList(ns+"getCartStoreList", userId);
	}

	@Override
	public List<MenuDTO> getMenu(int storeId, int menuId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", storeId);
		params.put("menuId", menuId);
		
		return ses.selectList(ns+"getMenu",params);
	}

	@Override
	public CartDTO getCartItem(int userId, int menuId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("menuId", menuId);
		return ses.selectOne(ns+"getCartItem", params);
	}

	@Override
	public int updateCart(CartDTO existingCart) throws Exception {
		return ses.update(ns+"updateCart",existingCart);
	}


}
