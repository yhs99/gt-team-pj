package com.team.goott.owner.menu.persistence;

import java.util.List;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuDAO {

	List<MenuDTO> getAllMenu(int storeId);

	int deleteMenu(int menuId, int storeId);

	MenuDTO getMenu(int menuId);

	int uploadMenu(MenuDTO uploadMenu);

	int updateMenu(int menuId, MenuDTO menu);

}
