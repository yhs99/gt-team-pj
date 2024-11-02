package com.team.goott.admin.store.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.admin.domain.AdminDTO;
import com.team.goott.admin.domain.StoresVO;
import com.team.goott.admin.store.service.AdminStoreService;
import com.team.goott.infra.StoreNotFoundException;
import com.team.goott.owner.domain.FacilityDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.StoreCategoryDTO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.store.service.OwnerStoreService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class AdminStoreController {

	private final String UNAUTHORIZED_MESSAGE = "권한이 없습니다.";
	
	@Autowired
	private AdminStoreService adminStoreService;
	
	@Autowired
	private OwnerStoreService ownerStoreService;
	
	@GetMapping("/searchStores")
	public ResponseEntity<Object> getStoresInfo(HttpSession session
												, @RequestParam(name = "categoryId", required = false) List<String> categoryId
												, @RequestParam(name = "sidoCodeId", required = false) List<String> sidoCodeId
												, @RequestParam(name = "searchParam", required = false) String searchParam
												, @RequestParam(name = "showBlock", required = false) String showBlock) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<StoresVO> stores = adminStoreService.getStoresInfo(categoryId, sidoCodeId, searchParam, showBlock);
		resultMap.put("storeLists", stores);
		resultMap.put("storeCount", stores.size());
		return ResponseEntity.ok(resultMap);
	}
	
	@DeleteMapping("admin/store/{storeId}")
	public ResponseEntity<Object> blockStore(HttpSession session
											, @PathVariable("storeId") int storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.blockStore(storeId) == 1 ? "블럭처리 되었습니다." : "서버 오류로 인해 블럭처리 되지 않았습니다.");
			}catch(StoreNotFoundException e) {
				return ResponseEntity.ok(e.getMessage());
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	@PatchMapping("admin/store/{storeId}")
	public ResponseEntity<Object> blockCancel(HttpSession session
											, @PathVariable("storeId") int storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.cancelBlock(storeId) == 1 ? "블럭해제 처리 되었습니다." : "서버 오류로 인해 블럭해제 처리 되지 않았습니다.");
			}catch(StoreNotFoundException e) {
				return ResponseEntity.ok("해당 블럭 처리된 매장을 찾을 수 없습니다.");
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	@GetMapping({"admin/stats/sales", "admin/stats/sales/{storeId}"})
	public ResponseEntity<Object> salesSummary(HttpSession session
											, @PathVariable(required = false) Integer storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.getSummary(storeId == null ? 0 : storeId));
			}catch(StoreNotFoundException e) {
				return ResponseEntity.ok("해당 매장을 찾을 수 없습니다.");
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	@GetMapping("admin/store/{storeId}")
	public ResponseEntity<Object> getStoreInfo(HttpSession session
											, @PathVariable(required = true) int storeId) {
		if(checkAdminSession(session)) {
			try {
				return ResponseEntity.ok(adminStoreService.getStoreInfoForUpdate(storeId));
			}catch(StoreNotFoundException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 매장을 찾을 수 없습니다.");
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
			}
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UNAUTHORIZED_MESSAGE);
		}
	}
	
	@PutMapping("admin/store/{storeId}")
    public ResponseEntity<Object> updateStore(
            HttpSession session,
            @PathVariable int storeId,
            @Valid @RequestPart("storeDTO") StoreDTO store,
            @RequestPart(value = "scheduleDTO", required = false) List<ScheduleDTO> schedules,
            @RequestPart(value = "storeCategoryDTO", required = false) List<StoreCategoryDTO> category,
            @RequestPart(value = "facilityDTO", required = false) List<FacilityDTO> facility,
            @RequestPart(value = "file", required = false) List<MultipartFile> updateFiles,
            @RequestPart(value = "deletedImageUrls", required = false) List<Object> deleteImages) throws Exception {
    	
    	// 삭제 요청받은 fileName을 저장하는 리스트
        List<String> deleteImage = new ArrayList<>();
        
        // deleteImages가 null이 아니고 비어있지 않은지 확인
        if (deleteImages != null && !deleteImages.isEmpty()) {
            for (Object obj : deleteImages) {
                if (obj instanceof String) {
                    deleteImage.add((String) obj); // Object를 String으로 캐스팅하여 추가
                }
            }
        }
    	
        if (!checkAdminSession(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (ownerStoreService.getStoreById(storeId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가게를 찾을 수 없습니다.");
        }
        // 가게 수정
        try {
            int result = ownerStoreService.updateStore(storeId, store, schedules, category, facility, updateFiles, deleteImage);
            
            if (result == 1) {
                return ResponseEntity.ok("가게가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 수정에 실패하였습니다.");
            }
        } catch (Exception e) {
            log.error("가게 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가게 수정 중 오류가 발생하였습니다.");
        }
    }
	
	public boolean checkAdminSession(HttpSession session) {
		AdminDTO adminSession = (AdminDTO) session.getAttribute("admin");
		if(adminSession == null) {
			return false;
		}
		return true;
	}
}
