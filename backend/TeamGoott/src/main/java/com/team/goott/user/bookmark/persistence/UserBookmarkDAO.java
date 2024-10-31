package com.team.goott.user.bookmark.persistence;

import java.util.List;

import com.team.goott.user.domain.BookmarkDTO;

public interface UserBookmarkDAO {

int save(BookmarkDTO bookmark);

int remove(BookmarkDTO bookmark);

List<BookmarkDTO> selectBookmarkByUserId(int userId);


}