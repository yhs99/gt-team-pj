package com.team.goott.owner.menu.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuService {

	List<MenuDTO> getAllMenu(boolean isMain, int storeId);

	int deleteMenu(int menuId, int storeId);

	MenuDTO getMenu(int menuId);

	int uploadMenu(MenuDTO menu, MultipartFile file);

	int updateMenu(int menuId, MenuDTO updateMenu, MultipartFile file, MenuDTO originMenu);

}
