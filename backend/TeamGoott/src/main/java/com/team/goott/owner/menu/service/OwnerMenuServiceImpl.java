package com.team.goott.owner.menu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.team.goott.owner.domain.MenuDTO;
import com.team.goott.owner.menu.persistence.OwnerMenuDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OwnerMenuServiceImpl implements OwnerMenuService {
	
	@Inject
	OwnerMenuDAO menuDAO;
	
	@Override
	public Map<String, Object> getAllMenu(int storeId) {
		Map<String, Object> allMenuInfo = new HashMap<String, Object>();
		List<MenuDTO> dishes = menuDAO.getAllMenu(storeId);
		int numOfMain = 0;
		int numOfSide = 0;
		for(MenuDTO dish : dishes) {
			if(dish.isMain()) {
				allMenuInfo.put("main", dish);
				numOfMain++;
			}else {
				allMenuInfo.put("side", dish);
				numOfSide++;
			}
		}
		allMenuInfo.put("numOfAllMenu", dishes.size());
		allMenuInfo.put("numOfMainMenu", numOfMain);
		allMenuInfo.put("numOfSideMenu", numOfSide);
		
		log.info("{}", allMenuInfo.toString());
		
		return allMenuInfo;
	}

	@Override
	public int deleteMenu(int menuId,int storeId) {
		return menuDAO.deleteMenu(menuId, storeId);
	}

	@Override
	public MenuDTO getMenu(int menuId) {
		return menuDAO.getMenu(menuId);
	}

	@Override
	public int uploadMenu(MenuDTO uploadMenu) {
		return menuDAO.uploadMenu(uploadMenu);
	}

	@Override
	public int updateMenu(int menuId, MenuDTO menu) {
		return menuDAO.updateMenu(menuId, menu);
	}

}
