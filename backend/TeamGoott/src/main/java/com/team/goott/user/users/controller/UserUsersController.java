package com.team.goott.user.users.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.service.OwnerStoreService;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.users.service.UserUsersService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserUsersController {

    @Inject
    private UserUsersService usersService;

    @Inject
    private OwnerStoreService ownerService;

    @PostMapping("/users/login")
    public ResponseEntity<Object> userLoginRequest(@RequestBody Map<String, String> loginData, HttpSession session, HttpServletResponse response) {
        Map<String, String> loginResponse = new HashMap<>();

        String loginGroup = loginData.get("loginGroup");
        String email = loginData.get("email");
        String password = loginData.get("upw");

        if ("0".equals(loginGroup)) {
            return handleUserLogin(email, password, session, response, loginResponse);
        } else if ("1".equals(loginGroup)) {
            return handleOwnerLogin(email, password, session, response);
        } else if ("99".equals(loginGroup)) {
            return ResponseEntity.ok("관리자 로그인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 로그인 그룹입니다.");
        }
    }

    private ResponseEntity<Object> handleUserLogin(String email, String password, HttpSession session, HttpServletResponse response, Map<String, String> loginResponse) {
        UserDTO loginInfo = usersService.login(email, password);

        if (loginInfo != null) {
            loginResponse.put("loginSuccess", loginInfo.getName());
            session.setAttribute("user", loginInfo);
            log.info("로그인 성공 세션 생성 : {}", loginInfo);
            log.info("로그인 성공 세션 아이디 : {}", session.getId());

            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(1800); // 30분 = 1800초
            response.addCookie(cookie);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 아이디 또는 비밀번호입니다.");
        }
    }

    private ResponseEntity<Object> handleOwnerLogin(String email, String password, HttpSession session, HttpServletResponse response) {
        StoreDTO storeInfo = ownerService.login(email, password);

        if (storeInfo != null) {
            session.setAttribute("store", storeInfo);
            log.info("로그인 성공 세션 생성 : {}", storeInfo);
            log.info("로그인 성공 세션 아이디 : {}", session.getId());

            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(1800); // 30분 = 1800초
            response.addCookie(cookie);

            return ResponseEntity.ok("ownerSuccess:" + storeInfo.getStoreId());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 아이디 또는 비밀번호입니다.");
        }
    }
}
