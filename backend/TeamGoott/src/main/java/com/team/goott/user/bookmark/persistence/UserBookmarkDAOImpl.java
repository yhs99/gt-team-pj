package com.team.goott.user.bookmark.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.BookmarkDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class UserBookmarkDAOImpl implements UserBookmarkDAO {

@Inject
private SqlSession ses;

private static String ns="com.team.mappers.user.bookmark.userBookmarkMapper.";

@Override
public int save(BookmarkDTO bookmark) {
	//저장
	return ses.insert(ns+"insertBookmark", bookmark);

}

@Override
public int remove(BookmarkDTO bookmark) {
	// 삭제
	return ses.delete(ns+"deleteBookmark",bookmark);
}

@Override
public List<BookmarkDTO> selectBookmarkByUserId(int userId) {
	// 조회
	return ses.selectList(ns+"selectBookmark", userId);
}


}