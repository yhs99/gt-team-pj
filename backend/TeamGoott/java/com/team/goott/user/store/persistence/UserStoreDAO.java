package com.team.goott.user.store.persistence;

import java.util.List;

import com.team.goott.user.domain.StoreDTO;

public interface UserStoreDAO {

	List<StoreDTO> getAll() throws Exception;

}
