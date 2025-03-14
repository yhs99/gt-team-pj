package com.team.goott.user.bookmark.persistence;

import java.util.List;

import com.team.goott.user.domain.BookmarkDTO;
import com.team.goott.user.domain.BookmarkInfoDTO;
import com.team.goott.user.domain.TodayInfoDTO;

public interface UserBookmarkDAO {

int save(BookmarkDTO bookmark);

int remove(BookmarkDTO bookmark);

List<BookmarkDTO> selectBookmarkByUserId(int userId);

BookmarkInfoDTO selectInfoByStoreId(TodayInfoDTO today);

int countBookmarks(int userId);


}