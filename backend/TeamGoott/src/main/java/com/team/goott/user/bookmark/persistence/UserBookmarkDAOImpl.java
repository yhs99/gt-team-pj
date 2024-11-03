package com.team.goott.user.bookmark.persistence;

import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.team.goott.user.domain.BookmarkDTO;
import com.team.goott.user.domain.BookmarkInfoDTO;
import com.team.goott.user.domain.TodayInfoDTO;

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
	log.info("dao:"+bookmark.toString());
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

@Override
public BookmarkInfoDTO selectInfoByStoreId(TodayInfoDTO today) {
	// 조회시 정보
	log.info(today.toString());
	return ses.selectOne(ns+"selectBookmarkInfo",today);
}

@Override
public int countBookmarks(int userId) {
	// TODO Auto-generated method stub
	return ses.selectOne(ns+"countBookmarks", userId);
}


}