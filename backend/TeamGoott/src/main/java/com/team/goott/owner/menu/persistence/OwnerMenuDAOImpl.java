package com.team.goott.owner.menu.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.owner.domain.MenuDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class OwnerMenuDAOImpl implements OwnerMenuDAO {
	
	@Inject
	private SqlSession ses;
	
	private static String ns = "com.team.mappers.owner.menu.ownerMenuMapper.";
	
	@Override
	public List<MenuDTO> getAllMenu(boolean isMain, int storeId) {
		Map<String, Object>args = new HashMap<String, Object>();
		args.put("isMain", isMain);
		args.put("storeId", storeId);
		return ses.selectList(ns+"getAllMenu", args);
	}

	@Override
	public int deleteMenu(int menuId, int storeId) {
		Map<String, Integer>args = new HashMap<String, Integer>();
		args.put("menuId", menuId);
		args.put("storeId", storeId);
		return ses.delete(ns+"deleteMenu", args);
	}

	@Override
	public MenuDTO getMenu(int menuId) {
		return ses.selectOne(ns+"getMenu", menuId);
	}

	@Override
	public int uploadMenu(MenuDTO uploadMenu) {
		return ses.insert(ns+"uploadMenu", uploadMenu);
	}

	@Override
	public int updateMenu(int menuId, MenuDTO menu) {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("menuId", menuId);
		args.put("menuName", menu.getMenuName());
		args.put("price", menu.getPrice());
		args.put("menuImageUrl", menu.getMenuImageUrl());
		args.put("menuImageName", menu.getMenuImageName());
		args.put("description", menu.getDescription());
		args.put("isMain", menu.isMain());
		return ses.update(ns+"updateMenu", args);
	}

}
