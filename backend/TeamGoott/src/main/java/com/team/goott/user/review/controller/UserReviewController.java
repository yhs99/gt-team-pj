package com.team.goott.user.review.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	//업로도하는 파일
	private List<reviewImagesDTO> reviewImageslst = new ArrayList<reviewImagesDTO>();
	// 수정된 파일
	private List<reviewImagesDTO> modifyFileList = new ArrayList<reviewImagesDTO>();
	
	@Autowired
	private UserReviewService service;
	
	 @GetMapping("/store/{storeId}")
	    public ResponseEntity<List<ReviewDTO>> getAllReview(@PathVariable("storeId") int storeId){
		 //모든 리뷰 기져오기
		 List<ReviewDTO> lst = null;

	    	lst = service.getAllReviews(storeId);
	
		return ResponseEntity.ok(lst);
	    }
	
	 
	 @GetMapping({"/{reviewId}","/{reviewId}/mod"})
	 	public ResponseEntity<ReviewDTO> viewBoard(@PathVariable("reviewId") int reviewId,
	 			HttpServletRequest request){
		 //리뷰 상세정보
		 ReviewDTO  reviewDTO= service.reviewByNo(reviewId);
		 
		 if(request.getRequestURI().endsWith("/mod")) {
			
			 this.modifyFileList = reviewDTO.getReviewImages();
			 System.out.println("이 리뷰의 파일 목록");
			 showModifyList();
			 
		}else {//수정 버튼을 눌렀을 때 파일 목록을 가져옴
			log.info("{}번 글 조회",reviewId);
		
		//썸네일
			
		
		
		}
	
		return ResponseEntity.ok(reviewDTO);
	 }
	 
	 
	 @PostMapping("/")
	 public ResponseEntity<String> addReview(@RequestPart("review") ReviewDTO comingDTO, @RequestPart("file")List<MultipartFile> files){
		 //리뷰 추가 //파일들은 뷰단에서 inputImageslist담아서 가져옴
		ResponseEntity<String> resEntity = null;
		reviewImagesDTO rImgDTO= null;
		int reviewId = comingDTO.getReviewId();
	
		if(files !=null ) {
		//s3파일 생성 & url & fileName & fileType & reviewId 설정
		for(MultipartFile file : files) {
			try {
				System.out.println("파일 이름 : " + file.getOriginalFilename());
				rImgDTO = service.imageIntoDTO(reviewId, file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			reviewImageslst.add(rImgDTO);
			
			if(rImgDTO)
		}
		
		comingDTO.setReviewImages(reviewImageslst);
		}
		
		 if(service.addReview(comingDTO)) {
			 
			 resEntity = new ResponseEntity<String>("add_succeeded",HttpStatus.OK);
			 System.out.println("저장완료");
		 }else {
			 resEntity = new ResponseEntity<String>("add_failed",HttpStatus.BAD_REQUEST);
			 System.out.println("저장실패");
		 }
//		 
		 reviewImageslst.clear();
//		
		 return resEntity;
	 }
	 

	 
	 @PutMapping("/{reviewId}")
	 public ResponseEntity<ReviewDTO> modReview(@PathVariable("reviewId") int reviewId,@RequestPart("review") ReviewDTO reviewDTO,
			 @RequestPart(value="file",required = false) MultipartFile[] files){
		 //리뷰 수정/근데 사실 아이디가 필요엄슴
		 //to do: 파일 중복검사 & 썸네일
		 
		
		 log.info("리뷰 수정 : " + reviewId + "/" + reviewDTO.toString());
		
		 reviewDTO.setReviewId(reviewId);
		 ReviewDTO newReview = new ReviewDTO();
				 
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reviewDTO.setReviewImages(modifyFileList);
		service.updateReview(reviewDTO);
		modifyFileList.clear();
		
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
	 public ResponseEntity<String> delReview(@PathVariable("reviewId") int reviewId, ReviewDTO reviewDto,HttpSession session){
		 //리뷰 삭제 //reviewDTO를 옵셔널로 바꿔줘야 하나?
		 ResponseEntity<String> resEntity = null;
//		 String fileName = imgDto.getFileName();
		 List<reviewImagesDTO> imgDtolst= reviewDto.getReviewImages();
		 if(service.deleteReviewNFile(reviewId,imgDtolst)) {
			 
			 resEntity = new ResponseEntity<String>("del_succeeded",HttpStatus.OK);
		 }else {
		
			 resEntity = new ResponseEntity<String>("del_failed",HttpStatus.NOT_FOUND);
		}
		 
		 return resEntity;
	 }
	 
	// 아직 최종 수정이 될지는 미지수 상태 : 하드에서 삭제할 수 없는 단계
	// 삭제 될 파일을 체크만 해두고 최종 요청이 들어오면 그 때 실제 삭제처리를 해야 한다.
	 @PostMapping("/removeAFile")
	 public ResponseEntity<String> modifyRemoveAFile(@RequestParam("removeFileNo") int removeFileNo){
	
		 System.out.println(removeFileNo+"번 파일 삭제 요청");
		 
		 for(reviewImagesDTO img : this.modifyFileList) {//원래 있던 리스트를 돌려야..
			 if(img.getImageId() == removeFileNo) {
				 img.setFileStatus(reviewImagesStatus.DELETE);
			 }
			 System.out.println("modifylist에 딜리트 추가");
			 showModifyList();
		 }
		return ResponseEntity.ok(removeFileNo+"번 파일 삭제 대기");
		 
	 }

}
