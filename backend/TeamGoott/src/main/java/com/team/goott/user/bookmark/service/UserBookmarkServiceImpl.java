
package com.team.goott.user.bookmark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.goott.user.bookmark.persistence.UserBookmarkDAO;
import com.team.goott.user.domain.BookmarkDTO;

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

        if(bDao.save(bookmark) == 1) {
        	result = true;
        }
   return result;
}

@Override
public boolean removeBookmark(int userId, int storeId) {
	//즐겨찾기 삭제
	boolean result = false;
	BookmarkDTO bookmark = new BookmarkDTO(userId, storeId);
	if(bDao.remove(bookmark) ==1) {
      	result = true;
      }

	return false;
}

@Override
public List<BookmarkDTO> getBookmarksByUserId(int userId) {
	// 즐겨찾기 조회
	 return bDao.selectBookmarkByUserId(userId);
}


}