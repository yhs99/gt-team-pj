package com.team.goott.user.bookmark.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.bookmark.service.UserBookmarkService;
import com.team.goott.user.domain.BookmarkDTO;
import com.team.goott.user.domain.BookmarkInfoDTO;
import com.team.goott.user.domain.UserDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/bookmark")
public class UserBookmarkController {


@Inject
UserBookmarkService service;

private static final int MAXNUM =30;

@PostMapping("/{storeId}")
public ResponseEntity<Object> addBookmark(@PathVariable("storeId") int storeId, HttpSession session) {
	// 사용자가 즐겨찾기를 추가하며, 최대 개수를 30개로 제한
    UserDTO user = (UserDTO) session.getAttribute("user");

    if (user == null) {
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
    }

    int userId = user.getUserId();

    int bookMarkCount = service.countBookMark(userId);
    if (bookMarkCount >= MAXNUM) {
        return ResponseEntity.badRequest().body("즐겨찾기는 30개 이상 하실 수 없습니다.");
    }

    try {
        if (service.addBookMark(userId, storeId)) {
            return ResponseEntity.ok("즐겨찾기에 추가했습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("즐겨찾기 추가에 실패했습니다.");
        }
    } catch (DuplicateKeyException | MyBatisSystemException e) {
        // 중복된 키가 있으면
        return ResponseEntity.badRequest().body("이미 즐겨찾기에 있습니다.");
    } catch (DataAccessException e) {
        return ResponseEntity.badRequest().body("존재하지 않는 가게입니다.");
    } catch (Exception e) {
        // 다른 예외 처리
    	 System.err.println("서버 오류 발생: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류입니다.");
    }
}


@DeleteMapping("/{storeId}")
public ResponseEntity<String> removeBookmark(@PathVariable("storeId") int storeId, HttpSession session) {
    // 즐겨찾기 삭제
    UserDTO user = (UserDTO) session.getAttribute("user");

    if (user == null) {
    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
    }

    int userId = user.getUserId();

    try {
        if (service.deleteBookmark(userId, storeId)) {
            return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
        } else {
            throw new NoSuchElementException("즐겨찾기에 존재하지 않는 가게입니다."); 
        }
    } catch (NoSuchElementException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류입니다.");
    }
}


@GetMapping("/")
public ResponseEntity<Object> getBookmark(HttpSession session) {
	//즐겨찾기 조회
	UserDTO user = (UserDTO) session.getAttribute("user");
	
	if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
	}
	
	int userId = user.getUserId();
	
	try {
	        List<BookmarkInfoDTO> lst = service.getBookmarkInfoByUserId(userId);
	        return ResponseEntity.ok(lst);
	   } catch (Exception e) {
	        // 예외 발생 시 로그 기록
	        System.err.println("즐겨찾기 조회 중 오류 발생: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류입니다."); 
	   }
	}
}

