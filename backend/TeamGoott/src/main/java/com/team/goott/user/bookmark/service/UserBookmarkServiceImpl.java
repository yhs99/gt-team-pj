
package com.team.goott.user.bookmark.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.user.bookmark.persistence.UserBookmarkDAO;
import com.team.goott.user.domain.BookmarkDTO;
import com.team.goott.user.domain.BookmarkInfoDTO;
import com.team.goott.user.domain.TodayInfoDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserBookmarkServiceImpl implements UserBookmarkService {

@Autowired
private UserBookmarkDAO bDao;

public boolean addBookMark(int userId, int storeId) {
    // 즐겨찾기 추가
    BookmarkDTO bookmark = new BookmarkDTO(userId, storeId);
    boolean result = false;
    log.info(bookmark.toString());
        if(bDao.save(bookmark) == 1) {
        	result = true;
        }
   return result;
}

@Override
public boolean deleteBookmark(int userId, int storeId) {
	//즐겨찾기 삭제
	BookmarkDTO bookmark = new BookmarkDTO(userId, storeId);
	boolean result = false;
	
	if(bDao.remove(bookmark) ==1) {
      	result = true;
      }

	return result;
}

@Override
public List<BookmarkInfoDTO> getBookmarkInfoByUserId(int userId) {
	// 즐겨찾기 조회
	List<BookmarkDTO> dtos = bDao.selectBookmarkByUserId(userId);
	List<BookmarkInfoDTO> info = new ArrayList<>();
	
	for(BookmarkDTO dto : dtos) {
		int storeId = dto.getStoreId();
		BookmarkInfoDTO infoDto = new BookmarkInfoDTO();
		TodayInfoDTO today = new TodayInfoDTO(storeId);
		infoDto = bDao.selectInfoByStoreId(today);
	
		if(infoDto != null) {
		
		infoDto.setBookmarkDto(dto);
		info.add(infoDto);
		}
	}
	return info;
	
}

@Override
public int countBookMark(int userId) {
	// 유저의 북마크 개수 가져오기
	return bDao.countBookmarks(userId);
}



}