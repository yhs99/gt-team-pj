package com.team.goott.owner.menu.service;

import java.util.List;
import java.util.Map;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuService {

	Map<String, Object> getAllMenu(int storeId);

	int deleteMenu(int menuId, int storeId);

	MenuDTO getMenu(int menuId);

	int uploadMenu(MenuDTO uploadMenu);

	int updateMenu(int menuId, MenuDTO menu);

}
