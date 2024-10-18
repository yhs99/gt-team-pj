package com.team.goott.owner.menu.persistence;

import java.util.List;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuDAO {

	List<MenuDTO> getAllMenu(boolean isMain, int storeId);

	int deleteMenu(int menuId, int storeId);

	MenuDTO getMenu(int menuId);

}
