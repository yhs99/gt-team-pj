package com.team.goott.user.review.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.jdbc.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.reviewImagesDTO;
import com.team.goott.user.domain.reviewImagesStatus;
import com.team.goott.user.review.service.UserReviewService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/review")
@Slf4j
public class UserReviewController { 
	
	private static final String ADD_SUCCEEDED = "add_succeeded";
	private static final String ADD_FAILED = "add_failed";
	private static final String DEL_SUCCEEDED = "del_succeeded";
	private static final String DEL_FAILED = "del_failed";
	private static final int MAX_NUM = 3;

	//업로도하는 파일
	private List<reviewImagesDTO> reviewImageslst = new ArrayList<reviewImagesDTO>();

	// 수정된 파일
	private List<reviewImagesDTO> modifyFileList = new ArrayList<reviewImagesDTO>();
	
	@Autowired
	private UserReviewService service;
	
	
	 @GetMapping("/store/{storeId}")
	 public ResponseEntity<List<ReviewDTO>> getAllReview(@PathVariable("storeId") int storeId, 
	    		@RequestParam(value = "page", defaultValue = "0") int page,
	            @RequestParam(value = "size", defaultValue = "5") int size){
		 //모든 리뷰 기져오기 (페이지네이션)
		 List<ReviewDTO> lst = service.getAllReviews(storeId, page, size);

		return ResponseEntity.ok(lst);
	    }
	
		/* my review */
	 @GetMapping("/user/{userId}")
	 public ResponseEntity<List<ReviewDTO>> getMyReview(@PathVariable("userId") int userId,
	 			@RequestParam(value = "page", defaultValue = "0") int page,
	            @RequestParam(value = "size", defaultValue = "5") int size){
		 
		 List<ReviewDTO> lst = service.getMyReview(userId,page,size);
		 
		 return ResponseEntity.ok(lst);
	 }
	 
	 
	 
	 @GetMapping({"/{reviewId}","/{reviewId}/mod"})
	 public ResponseEntity<ReviewDTO> viewBoard(@PathVariable("reviewId") int reviewId,
	 			HttpServletRequest request){
		 //리뷰 상세정보
		 ReviewDTO  reviewDTO= service.reviewByNo(reviewId);
		 
		 if(request.getRequestURI().endsWith("/mod")) {
			//수정 버튼을 눌렀을 때 파일 목록을 가져옴
			 this.modifyFileList = reviewDTO.getReviewImages();
			 System.out.println("이 리뷰의 파일 목록");
			 showModifyList();
			 
		}else {
			log.info("{}번 글 조회",reviewId);
		
		
		}
	
		return ResponseEntity.ok(reviewDTO);
	 }
	 
	 
	 @PostMapping("/")
	 public ResponseEntity<String> addReview(@RequestPart("review") ReviewDTO comingDTO, @RequestPart("file")List<MultipartFile> files){
		 //리뷰 추가 //파일들은 뷰단에서 inputImageslist담아서 가져옴
		ResponseEntity<String> resEntity = null;
		reviewImagesDTO rImgDTO= null;
		int reviewId = comingDTO.getReviewId();
		int reserveId = comingDTO.getReserveId();
		
		if(files !=null ) {
			  if (files.size() > MAX_NUM) {
		            return ResponseEntity.badRequest().body("파일은 최대 " + MAX_NUM + "개까지만 업로드할 수 있습니다.");
		        }
			
	   // 이미지 파일 체크
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (!isImageFile(contentType)) {
                return ResponseEntity.badRequest().body("업로드할 수 있는 파일 형식은 이미지 파일만 가능합니다.");
            }
        }
			
		//s3파일 생성 & url & fileName & fileType & reviewId 설정
		for(MultipartFile file : files) {
			try {
				System.out.println("파일 이름 : " + file.getOriginalFilename());

				rImgDTO = service.imageIntoDTO(reviewId, file);
				reviewImageslst.add(rImgDTO);
				
			} catch (Exception e) {
				 log.error("파일 처리 중 오류 발생: {}", e.getMessage());
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생: " + e.getMessage());
			}
		}
		comingDTO.setReviewImages(reviewImageslst);
		
		 if(service.addReviewPics(comingDTO)) {
			 resEntity = new ResponseEntity<String>(ADD_SUCCEEDED,HttpStatus.OK);
			 System.out.println("저장완료");
		 }else {
			 resEntity = new ResponseEntity<String>(ADD_FAILED,HttpStatus.BAD_REQUEST);
			 System.out.println("저장실패");
		 }
		 
		 reviewImageslst.clear();
		}else {
	        return ResponseEntity.badRequest().body("파일이 제공되지 않았습니다.");
	    }
		
		 return resEntity;
	 }
	 

	 
	 private boolean isImageFile(String contentType) {
		//  이미지 파일인지 확인
		  return contentType != null && (contentType.startsWith("image/"));
	}
	 

	@PutMapping("/{reviewId}")
	 public ResponseEntity<Object> modReview(@PathVariable("reviewId") int reviewId,@RequestPart("review") ReviewDTO reviewDTO,
			 @RequestPart(value="file",required = false) MultipartFile[] files){
		 //리뷰 수정
		
		 // 이미지 파일 체크
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (!isImageFile(contentType)) {
                return ResponseEntity.badRequest().body("업로드할 수 있는 파일 형식은 이미지 파일만 가능합니다.");
            }
        }
		
		 reviewDTO.setReviewId(reviewId);
		 
		   // modifyFileList의 현재 상태 확인 
		    int count = 0;
		    for (reviewImagesDTO img : modifyFileList) {
		        if (img.getFileStatus() == null || img.getFileStatus() == reviewImagesStatus.INSERT) {
		            count++;
		        }
		    }

		 
		 if (files != null && files.length > 0) {
			  // 새로 추가될 파일 개수를 카운트
		        count += files.length;

		        // 총 개수가 MAX_FILES를 초과하는지 체크
		        if (count > MAX_NUM) {
		            return ResponseEntity.badRequest().body("파일은 최대 " + MAX_NUM + "개까지만 업로드할 수 있습니다.");
		        }
		        
			 try {
				 for(MultipartFile li : files) {
					 //s3에 저장
					 reviewImagesDTO tmpDto = service.imageIntoDTO(reviewId, li);
					 //status insert로 변경
					 tmpDto.setFileStatus(reviewImagesStatus.INSERT);
					 this.modifyFileList.add(tmpDto);
				 }
				 showModifyList();
				 
				 
			 } catch (IOException e) {
				 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
			 } catch (Exception e) {
				 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
			 }
			 
			 reviewDTO.setReviewImages(modifyFileList);
			 service.updateReview(reviewDTO);
			 modifyFileList.clear();
			 
			 
			}else {
				
				service.updateReview(reviewDTO);
			}
				 
		return ResponseEntity.ok(reviewDTO);
	 }



	private void showModifyList() {
		System.out.println("=============modifyList 목록=============");
		for(reviewImagesDTO img : modifyFileList) {
			System.out.println(img.toString());
		}
		System.out.println("=======================================");
	}
	 
	 @DeleteMapping("/{reviewId}")
	 public ResponseEntity<String> delReview(@PathVariable("reviewId") int reviewId){
		 //리뷰 삭제 : isDelete 를 -1로 바꿔줌//
		 ResponseEntity<String> resEntity;
		 ReviewDTO reviewDto= service.reviewByNo(reviewId);
		 

		 if (reviewDto == null) {
		        return new ResponseEntity<>("리뷰 DTO가 null입니다.", HttpStatus.BAD_REQUEST);
		 }
		  
		 List<reviewImagesDTO> imgDtoList= reviewDto.getReviewImages();
		 
		  // imgDtoList가 null인 경우 빈 리스트로 초기화
		    if (imgDtoList == null) {
		        imgDtoList = new ArrayList<>();
		    }
		 
		 if(service.deleteReviewNFile(reviewId,imgDtoList)) {
			 
			 resEntity = new ResponseEntity<String>(DEL_SUCCEEDED,HttpStatus.OK);
		 }else {
		
			 resEntity = new ResponseEntity<String>(DEL_FAILED,HttpStatus.NOT_FOUND);
		}
		 
		 return resEntity;
	 }
	 
	//mod페이지에 먼저 들어갈 것
	 @PostMapping("/removeAFile")
	 public ResponseEntity<String> modifyRemoveAFile(@RequestParam("removeFileNo") int removeFileNo){
	
		 System.out.println(removeFileNo+"번 파일 삭제 요청");
		 
		 for(reviewImagesDTO img : this.modifyFileList) {
			 if(img.getImageId() == removeFileNo) {
				 img.setFileStatus(reviewImagesStatus.DELETE);
			 }
			 showModifyList();
		 }
		return ResponseEntity.ok(removeFileNo+"번 파일 삭제 대기");
		 
	 }
	 
	 //user의 reserveId가 
	 //reservestatusCode가 4인지 체크하고,  reserveId당 한번만 글작성이 가능하도록 하는 메서드
	 @PostMapping("/user/{userId}")
	 public ResponseEntity<Object> checkReserveDone(@PathVariable("userId") int userId) {
	     // 유저의 예약 상태 가져오기
	     List<ReserveDTO> reserves = service.getStatusByUserId(userId);
	     // 유저의 리뷰 목록 가져오기
	     List<ReviewDTO> reviews = service.getUserReview(userId);

	     // 리뷰 ID를 저장할 Set
	     Set<Integer> reviewedReserveIds = reviews.stream()
	         .map(ReviewDTO::getReserveId)
	         .collect(Collectors.toSet());

	     // 예약 체크
	     for (ReserveDTO userReserveInfo : reserves) {
	         int reserveStatus = userReserveInfo.getStatusCodeId();
	         int reserveId = userReserveInfo.getReserveId();

	         if (reserveStatus != 4) {
	             return ResponseEntity.badRequest().body("해당 리뷰는 처리할 수 없습니다. 아직 완료되지 않았습니다.");
	         }

	         // 예약 ID가 이미 리뷰에 포함되어 있는지 확인
	         if (reviewedReserveIds.contains(reserveId)) {
	             return ResponseEntity.badRequest().body("해당 리뷰는 이미 처리되었습니다.");
	         }
	     }

	     return ResponseEntity.ok("상태가 완료되었습니다. 식당 리뷰를 작성해주세요.");
	 }
	 
	 //로그인 확인은 생략 : 로그인이 되어야 마이페이지에 갈 수 있고 마이페이지에 가야 리뷰를 작성, 수정할 수 있으므로, 식당 페이지에서 리뷰를 수정하는건 조금 이상한듯(네이버 쇼핑 참고)
	 //review 테이블에 reserveId 컬럼을 하나 추가해서 reserveId로 쓴 리뷰가 있으면 막는 방법이 제일 낫긴하죠

}
