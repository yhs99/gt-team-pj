package com.team.goott.owner.menu.service;

import java.util.List;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuService {

	List<MenuDTO> getAllMenu(boolean isMain, int storeId);

	int deleteMenu(int menuId, int storeId);

}
