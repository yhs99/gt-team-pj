package com.team.goott.home.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles requests for the application home page.
 */
@RestController
@Slf4j
public class HomeController {
	
	
	@GetMapping("/test")
    public ResponseEntity<Object> apiTest(@RequestParam(defaultValue = "����") String param) {
		log.info("apiTest ȣ��!");
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		map.put("key2", "value2");
		List<String> arr = new ArrayList<String>();
		arr.add("1");
		arr.add("2");
		arr.add("3");
		returnMap.put("Key", param);
		returnMap.put("Key2", arr);
		
        return ResponseEntity.ok(returnMap);
    }

    @GetMapping("/error")
    public ResponseEntity<Object> errorTest() {
    	log.info("���� �׽�Ʈ ȣ��!");
        return ResponseEntity.badRequest().body("�����߻�");
    }
	
}
