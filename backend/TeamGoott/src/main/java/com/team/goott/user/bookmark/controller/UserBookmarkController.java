package com.team.goott.user.bookmark.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.user.bookmark.service.UserBookmarkService;
import com.team.goott.user.domain.BookmarkDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/bookmark")
public class UserBookmarkController {

	@Inject
	UserBookmarkService service;
	
	@PostMapping("/{storeId}")
	public ResponseEntity<String> addBookmark(@RequestParam("userId") int userId, @PathVariable("storeId") int storeId) {
	    // 즐겨찾기 추가 로직
	    if (service.addBookMark(userId, storeId)) {
	        return ResponseEntity.ok("즐겨찾기에 추가되었습니다.");
	    } else {
	        return ResponseEntity.badRequest().body("이미 즐겨찾기에 추가된 가게입니다.");
	    }
	}
	
	@DeleteMapping("/{storeId}")
	public ResponseEntity<String> removeBookmark(@RequestParam("userId") int userId, @PathVariable("storeId") int storeId) {
	    // 즐겨찾기 삭제 로직
	    if (service.removeBookmark(userId, storeId)) {
	        return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
	    } else {
	        return ResponseEntity.badRequest().body("즐겨찾기에 존재하지 않는 가게입니다.");
	    }
	}
	
	@GetMapping("/")
	public ResponseEntity<List<BookmarkDTO>> getBookmark(@PathVariable("userId") int userId) {
		//list
	    List<BookmarkDTO> bookmarks = service.getBookmarksByUserId(userId);
	    return ResponseEntity.ok(bookmarks);
	}
}
