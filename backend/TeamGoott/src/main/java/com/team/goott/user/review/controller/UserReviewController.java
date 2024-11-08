package com.team.goott.user.review.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewImagesDTO;
import com.team.goott.user.domain.UserDTO;
import com.team.goott.user.domain.ReviewImagesStatus;
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
private static final int MAX_NUM = 5;
private static final int MAX_CONTENT =255;

//업로도하는 파일
private List<ReviewImagesDTO> reviewImageslst = new ArrayList<ReviewImagesDTO>();

// 수정된 파일
private List<ReviewImagesDTO> modifyFileList = new ArrayList<ReviewImagesDTO>();

@Autowired
private UserReviewService service;

 //모든 리뷰 기져오기 (페이지네이션)
 @GetMapping("/store/{storeId}")
 public ResponseEntity<List<ReviewDTO>> getAllReview(@PathVariable("storeId") int storeId,
    		@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size){

	 List<ReviewDTO> lst = service.getAllReviews(storeId, page, size);

	return ResponseEntity.ok(lst);
    }

//모든 리뷰 가져오기-마이페이지(페이지네이션)
 @GetMapping("/")
 public ResponseEntity<Object> getMyReview(HttpSession session,
 			@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size){
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 List<ReviewDTO> lst = null;

	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 int userId = user.getUserId();
	 lst = service.getMyReview(userId,page,size);

	 return ResponseEntity.ok(lst);
 }

 //마이페이지에서 아직 안쓴 리뷰 보기
 @GetMapping("/toWrite")
 public ResponseEntity<Object> reviewsToWrite(HttpSession session) {
	 //로그인 확인
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 int userId = user.getUserId();
	 List<ReserveDTO> reservs = service.getStatusByUserId(userId);
	 List<ReserveDTO> newRes = new ArrayList<ReserveDTO>();

	 for(ReserveDTO reserve : reservs) {
		 if(reserve.getStatusCodeId() == 4) {
			 ReserveDTO res = new ReserveDTO();
			 res.setUserId(reserve.getUserId());
			 res.setReserveId(reserve.getReserveId());
			 res.setStatusCodeId(reserve.getStatusCodeId());
			 res.setStoreId(reserve.getStoreId());
			 newRes.add(res);
		 }
	 }

	return ResponseEntity.ok(newRes);
 }

 @GetMapping("/{reviewId}")
 public ResponseEntity<Object> viewReview(@PathVariable("reviewId") int reviewId,
 			HttpServletRequest request, HttpSession session){
	 //리뷰 수정(score/content/images)
	 ReviewDTO reviewDTO= service.reviewByNo(reviewId);

	 UserDTO user = (UserDTO) session.getAttribute("user");

		//수정 버튼을 눌렀을 때 파일 목록을 가져옴

		 if(user == null) {
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
		 }

		 if(user.getUserId() != reviewDTO.getUserId()){
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("작성자와 사용자가 일치하지 않습니다.");
		 }

		 log.info("{}번 글 수정",reviewId);

		 this.modifyFileList = reviewDTO.getReviewImages();
		 System.out.println("이 리뷰의 파일 목록");
		 showModifyList();

	return ResponseEntity.ok(reviewDTO);
 }

 // 먼저 mypage에서 쓸 수 있는 리뷰를 확인하는 메서드인 reviewsToWrite()에서 reservId를 받아옴)
 @PostMapping("/")
 public ResponseEntity<Object> insertReview(@RequestPart("review") ReviewDTO comingDTO, @RequestPart("file")List<MultipartFile> files
		 ,HttpSession session){
	 //리뷰 추가 //파일들은 뷰단에서 inputImageslist담아서 가져옴
	ReviewImagesDTO rImgDTO= null;
	//로그인 확인
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 int userId = user.getUserId();
	 int reserveId = comingDTO.getReserveId();
	 ReserveDTO reserveInfo = service.getReserveInfoByReserveId(reserveId);

  // 리뷰 작성 여부 및 예약 상태 체크
    ResponseEntity<Object> reviewCheckResponse = checkReviewDone(userId, reserveId);
    if (reviewCheckResponse.getStatusCode() != HttpStatus.OK) {
        return reviewCheckResponse; // 리뷰 작성이 완료된 경우 처리
    }

    ResponseEntity<Object> reserveCheckResponse = checkReserveStatus(userId, reserveId);
    if (reserveCheckResponse.getStatusCode() != HttpStatus.OK) {
        return reserveCheckResponse; // 예약 상태가 완료되지 않은 경우 처리
    }

    if(comingDTO.getStoreId() != reserveInfo.getStoreId()) {
    	return ResponseEntity.badRequest().body("예약과 리뷰의 가게가 일치하지 않습니다");
    }

	//파일 체크 및 처리
	if(files !=null ) {
		 if (files.size() > MAX_NUM) {
	            return ResponseEntity.badRequest().body("파일은 최대 " + MAX_NUM + "개까지만 업로드할 수 있습니다.");
	     }

  // 리뷰 내용 길이 체크
	    if (comingDTO.getContent() != null && comingDTO.getContent().length() > MAX_CONTENT) {
	        return ResponseEntity.badRequest().body("리뷰는 최대 " + MAX_CONTENT + "자까지 작성할 수 있습니다.");//255
	    }

   // 이미지 파일 체크
    for (MultipartFile file : files) {
        String contentType = file.getContentType();
        if (!isImageFile(contentType)) {
            return ResponseEntity.badRequest().body("업로드할 수 있는 파일 형식은 이미지 파일만 가능합니다.");
        }
    }

	//s3파일 생성 & url & fileName & fileType & reviewId 설정
    List<ReviewImagesDTO> reviewImageslst = new ArrayList<>();
	for(MultipartFile file : files) {
		try {
			System.out.println("파일 이름 : " + file.getOriginalFilename());
			rImgDTO = service.imageIntoDTO(comingDTO.getReviewId(), file);
			reviewImageslst.add(rImgDTO);

		} catch (Exception e) {
			 log.error("파일 처리 중 오류 발생: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생: " + e.getMessage());
		}
	}
	//파일 리스트 준비 완료
	comingDTO.setReviewImages(reviewImageslst);

	 // 리뷰 추가
    try {
        if (service.addReviewPics(comingDTO)) {
            System.out.println("저장완료");
            reviewImageslst.clear(); // 리뷰가 추가된 후 리스트 초기화
            return ResponseEntity.status(HttpStatus.OK).body(ADD_SUCCEEDED);
        } else {
            System.out.println("저장실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ADD_FAILED);
        }
    } catch (DuplicateKeyException | MyBatisSystemException e) {
        // 중복된 키가 있으면
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 작성한 리뷰가 있습니다.");
    }

	}else {
		return checkReserveStatus(userId, reserveId);
	}
 }

 private boolean isImageFile(String contentType) {
	//  이미지 파일인지 확인
	  return contentType != null && (contentType.startsWith("image/"));
}

@PutMapping("/{reviewId}")
 public ResponseEntity<Object> modReview(@PathVariable("reviewId") int reviewId,@RequestPart("review") ReviewDTO reviewDTO,
		 @RequestPart(value="file",required = false) MultipartFile[] files, HttpSession session){
	 //리뷰 수정(score/content/images)

	//로그인
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 //사용자 일치 검증
	 if(user.getUserId() != reviewDTO.getUserId()){
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("작성자와 사용자가 일치하지 않습니다.");
	 }

	 if(reviewDTO.isDeleteReq()) {
		 return ResponseEntity.status(HttpStatus.GONE).body("삭제된 글입니다.");
	 }

	 // 리뷰 내용 길이 체크
    if (reviewDTO.getContent() != null && reviewDTO.getContent().length() > MAX_CONTENT) {
        return ResponseEntity.badRequest().body("리뷰는 최대 " + MAX_CONTENT + "자까지 작성할 수 있습니다.");
    }

	 // 이미지 파일 체크
    if(files != null) {
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (!isImageFile(contentType)) {
                return ResponseEntity.badRequest().body("업로드할 수 있는 파일 형식은 이미지 파일만 가능합니다.");
            }
        }
    }

	 reviewDTO.setReviewId(reviewId);

	   // modifyFileList의 현재 상태 확인
	    int count = 0;
	    for (ReviewImagesDTO img : modifyFileList) {
	        if (img.getFileStatus() == null || img.getFileStatus() == ReviewImagesStatus.INSERT) {
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
	       // 새 파일 S3 저장 및 리스트에 추가
			 try {
				 for(MultipartFile file : files) {
					 //s3에 저장
					 ReviewImagesDTO tmpDto = service.imageIntoDTO(reviewId, file);
					 //status insert로 변경
					 tmpDto.setFileStatus(ReviewImagesStatus.INSERT);
					 this.modifyFileList.add(tmpDto);
				 }

				 showModifyList();

				 } catch (IOException e) {
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
				 } catch (Exception e) {
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
				 }

		}
			reviewDTO.setReviewImages(modifyFileList);
			service.updateReview(reviewDTO);
		    modifyFileList.clear();

	return ResponseEntity.ok(reviewDTO);
 }

private void showModifyList() {
	System.out.println("=============modifyList 목록=============");
	for(ReviewImagesDTO img : modifyFileList) {
		System.out.println(img.toString());
	}
	System.out.println("=======================================");
}

//리뷰 삭제 : db에서 사라지지 않고 isDelete 를 -1로 바꿔줌
 @DeleteMapping("/{reviewId}")
 public ResponseEntity<Object> delReview(@PathVariable("reviewId") int reviewId, HttpSession session){
	 ReviewDTO reviewDto= service.reviewByNo(reviewId);

	//로그인
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 if (reviewDto == null) {
	        return new ResponseEntity<>("리뷰 DTO가 null입니다.", HttpStatus.BAD_REQUEST);
	 }

	 //사용자 일치 검증
	 if(user.getUserId() != reviewDto.getUserId()){
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("작성자와 사용자가 일치하지 않습니다.");
	 }

	 if(reviewDto.isDeleteReq()) {
		 return ResponseEntity.status(HttpStatus.GONE).body("이미 삭제된 글입니다.");
	 }

	 List<ReviewImagesDTO> imgDtoList= reviewDto.getReviewImages();

	  // imgDtoList가 null인 경우 빈 리스트로 초기화
	    if (imgDtoList == null) {
	        imgDtoList = new ArrayList<>();
	    }

     int reserveId = reviewDto.getReserveId();

     if(service.deleteReviewNFile(reviewId, reserveId, imgDtoList)) {
		 return ResponseEntity.ok(DEL_SUCCEEDED);
	 }else {

		 return ResponseEntity.badRequest().body(DEL_FAILED);
	}

 }

//사진 위에 있는 x클릭
//mod페이지에 먼저 들어가야 modifylist가 생성된다
 @DeleteMapping("/{reviewId}/{imageId}")
 public ResponseEntity<String> modifyRemoveAFile(@PathVariable("reviewId") int reviewId, @PathVariable("imageId") int imageId, HttpSession session){

	//로그인
	 UserDTO user = (UserDTO) session.getAttribute("user");
	 if(user == null) {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다"); //401
	 }

	 //리뷰 이미지 존재 확인
	 if(service.checkImageExist(imageId) != 1) {
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 이미지입니다.");
	 }

	// 리뷰 이미지 목록 가져오기
	 List<ReviewImagesDTO> images = service.selectReviewImagesByReviewId(reviewId);

   // 해당 파일이 리뷰에 존재하는지 확인
	if(images != null) {
	    boolean fileExists = false;
	    for (ReviewImagesDTO image : images) {
	        if (image.getImageId() == imageId) {
	            fileExists = true;
	            break;
	        }
	    }

	    if (!fileExists) {
	        return ResponseEntity.badRequest().body("해당 리뷰에 없는 파일입니다.");
	    }
	}
	 System.out.println(imageId+"번 파일 삭제 요청");

	 for(ReviewImagesDTO img : this.modifyFileList) {
		 if(img.getImageId() == imageId) {
			 img.setFileStatus(ReviewImagesStatus.DELETE);
		 }
		 showModifyList();
	 }
	return ResponseEntity.ok(imageId+"번 파일 삭제 대기");

 }

 //reservestatusCode가 4인지 체크하고,  reserveId당 한번만 글작성이 가능하도록 하는 메서드
 public ResponseEntity<Object> checkReviewAndReserveStatus(int userId,int reservationId, boolean checkReview) {
	    // 유저의 예약 상태 가져오기
	    List<ReserveDTO> reserves = service.getStatusByUserId(userId);
	    // 유저의 리뷰 목록 가져오기
	    List<ReviewDTO> reviews = service.getUserReview(userId);
	    //reserveId로 reserveDto가져오기
	    ReserveDTO reserveDto = service.getReserveInfoByReserveId(reservationId);
	    int userIdFromReserve = reserveDto.getUserId();

	    if(userIdFromReserve != userId) {
	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("작성자와 사용자가 일치하지 않습니다.");
	    }

	    // 리뷰에 있는 reserveID를 저장할 Set(삭제되지 않은 리뷰들 중에서)
	    Set<Integer> reviewedReserveIds = reviews.stream()
	        .map(ReviewDTO::getReserveId)
	        .collect(Collectors.toSet());

	    int reserveStatus = reserveDto.getStatusCodeId();

	    // 예약 체크
        if (checkReview) {
            // 리뷰 작성 여부 체크
            if (reviewedReserveIds.contains(reservationId)) {
                log.info("해당 예약건의 리뷰를 이미 작성하셨습니다.");
                return ResponseEntity.ok("해당 예약건의 리뷰를 이미 작성하셨습니다.");
            }
        }
         if(!checkReview){
            // 예약 상태 체크
            if (reserveStatus == 4) {
            	log.info("상태가 완료되었습니다. 식당 리뷰를 작성해주세요.");
            	return ResponseEntity.ok("상태가 완료되었습니다. 식당 리뷰를 작성해주세요.");
            }else if(reserveStatus == 5) {
            	log.info("이미 리뷰를 작성하셨습니다.");
            	return ResponseEntity.badRequest().body("이미 리뷰를 작성하셨습니다.");
            }else {
            	  return ResponseEntity.badRequest().body("상태가 완료될 때까지 기다려주세요.");
            }
        }

		return  ResponseEntity.ok("상태 확인 완료.");
 }

	// 이미 예약에 대해 리뷰작성이 끝났는지 확인
	public ResponseEntity<Object> checkReviewDone(int userId, int reserveId) {
	    return checkReviewAndReserveStatus(userId, reserveId, true);
	}

	// ReserveStatus가 4(완료)인지 확인
	public ResponseEntity<Object> checkReserveStatus(int userId, int reserveId) {
	    return checkReviewAndReserveStatus(userId, reserveId, false);
	}




}