package com.team.goott.owner.menu.service;

import java.util.List;

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
	public List<MenuDTO> getAllMenu(boolean isMain, int storeId) {
		
		return menuDAO.getAllMenu(isMain, storeId);
	}

	@Override
	public int deleteMenu(int menuId,int storeId) {
		return menuDAO.deleteMenu(menuId, storeId);
	}

}
