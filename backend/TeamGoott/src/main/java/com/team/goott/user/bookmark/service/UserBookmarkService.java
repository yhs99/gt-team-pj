package com.team.goott.user.bookmark.service;

import java.util.List;

import com.team.goott.user.domain.BookmarkDTO;

public interface UserBookmarkService {

	boolean addBookMark(int userId, int storeId);

	boolean removeBookmark(int userId, int storeId);

	List<BookmarkDTO> getBookmarksByUserId(int userId);

}
