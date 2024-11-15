package com.team.goott.owner.menu.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.owner.domain.MenuDTO;

public interface OwnerMenuService {

	Map<String, Object> getAllMenu(int storeId);

	int deleteMenu(int menuId, int storeId);

	MenuDTO getMenu(int menuId);

	int uploadMenu(MenuDTO menu, MultipartFile file, int storeId);

	int updateMenu(int menuId, MenuDTO updateMenu, MultipartFile file, MenuDTO originMenu);

	int updateMenuWithoutFile(int menuId, MenuDTO updateMenu, MenuDTO originMenu);

}
