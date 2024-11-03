package com.team.goott.user.bookmark.service;

import java.util.List;

import com.team.goott.user.domain.BookmarkDTO;
import com.team.goott.user.domain.BookmarkInfoDTO;

public interface UserBookmarkService {

boolean addBookMark(int userId, int storeId);

boolean deleteBookmark(int userId, int storeId);

List<BookmarkInfoDTO> getBookmarkInfoByUserId(int userId);

int countBookMark(int userId);

}
