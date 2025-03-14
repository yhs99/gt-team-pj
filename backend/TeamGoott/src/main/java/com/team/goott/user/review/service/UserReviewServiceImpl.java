package com.team.goott.user.review.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.team.goott.infra.S3ImageManager;
import com.team.goott.owner.domain.NotificationDTO;
import com.team.goott.owner.domain.NotificationType;
import com.team.goott.user.domain.ReserveDTO;
import com.team.goott.user.domain.ReviewDTO;
import com.team.goott.user.domain.ReviewImagesDTO;
import com.team.goott.user.domain.ReviewPageDTO;
import com.team.goott.user.domain.ReviewImagesStatus;
import com.team.goott.user.review.persistence.UserReviewDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserReviewServiceImpl implements UserReviewService {


  private static final String INVALID_FILENAME = "Invalid filename";

  @Autowired
  private AmazonS3 s3Client;

  private final String bucketName = "goott-bucket";

  @Autowired
  private UserReviewDAO revDAO;

	  @Override
	  public List<ReviewDTO> getAllReviews(int storeId, String sort, int page, int size) {
	    ReviewPageDTO paging = ReviewPageDTO.builder().storeId(storeId).page(page).size(size).build();
	    paging.setStartRow();
	    log.info(paging.toString());
	    List<ReviewDTO> lst = revDAO.getAllReviews(paging , sort);
	    for(ReviewDTO list : lst) {
	      log.info("{}",list.getCreateAtLocalDateTime().toString());
	      List<ReviewImagesDTO> images = selectReviewImagesByReviewId(list.getReviewId());
	      list.setReviewImages(images);
	      ReviewDTO userInfo = revDAO.selectUserByUserId(list.getUserId());
	      String userName = userInfo.getName();
	      String profileUrl = userInfo.getProfileImageUrl();
	      list.setName(userName);
	      list.setProfileImageUrl(profileUrl);
	    }
	    return lst;
	  }


	@Override
	public ReviewDTO reviewByNo(int reviewId) {
		// 리뷰 조회
		ReviewDTO review = revDAO.reviewByNo(reviewId);
		List<ReviewImagesDTO> imagesDTO = new ArrayList<ReviewImagesDTO>();
		imagesDTO = revDAO.filesByNo(reviewId);
		review.setReviewImages(imagesDTO);
		
		return review;
	}

	@Override
	@Transactional
	public boolean addReviewPics(ReviewDTO reviewDTO) {
		// 리뷰 추가
		boolean result=false;
		log.info("REVIEW DETAIL :: {}", reviewDTO.toString());
		if(revDAO.insertReview(reviewDTO)==1) {
			int generatedId = reviewDTO.getReviewId();
			//리뷰 등록시 해당 식당 점주에게 알림설정 
			int sendNotification = sendNotificationsToOwner(reviewDTO);
			if(sendNotification == 1) {
				log.info("알림설정 완료");
			}
			if(reviewDTO.getReviewImages() != null) {
				for(ReviewImagesDTO imgs : reviewDTO.getReviewImages()) {
					imgs.setReviewId(generatedId);
					revDAO.insertImgs(imgs);
				}
				log.info("이미지 저장 완료");
			}
		}
			
		if(revDAO.changeStatusCodeId(reviewDTO.getReserveId(), 5) > 0){
			
		result = true;
		log.info("리뷰 저장 완료");
		}else {
		    log.error("리뷰 저장 실패: 리뷰 추가에 실패했습니다.");
				
		}
		return result;
	}

  
	private int sendNotificationsToOwner(ReviewDTO reviewDTO) {
		NotificationDTO notification = new NotificationDTO();
		notification.setUserId(reviewDTO.getUserId());
		notification.setStoreId(reviewDTO.getStoreId());
		notification.setNotificationType(NotificationType.valueOf("CUSTOMER_TO_OWNER"));
		notification.setReserveId(reviewDTO.getReserveId());
		notification.setMessage("예약번호 : " + reviewDTO.getReserveId() +" 님께서 리뷰를 작성하셨습니다.");
		
		return revDAO.setNotification(notification);
	}
	
	@Override
	public ReviewImagesDTO imageIntoDTO(int reviewId, MultipartFile file) throws IOException, Exception {
		ReviewImagesDTO reviewImg = new ReviewImagesDTO();
		// 원본 이미지 저장
		log.info("리뷰아이디 : {}", reviewId);
		S3ImageManager imageManager = new S3ImageManager(file, s3Client, bucketName);
		Map<String, String> reviewImages = imageManager.uploadImage();
		String ext = "";
		String originalFileName=file.getOriginalFilename();
	
		if (originalFileName == null || !originalFileName.contains(".")) {
		    throw new IllegalArgumentException(INVALID_FILENAME);
		}else {
			ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
		}
	
		reviewImg.setUrl(reviewImages.get("imageUrl"));
		reviewImg.setFileName(reviewImages.get("imageFileName"));
		reviewImg.setFileType(ext);
		reviewImg.setReviewId(reviewId);
	
		log.info("reivewImage DTO : {} ", reviewImg.toString());
	
		return reviewImg;
	}

	

	@Override
	@Transactional
	public boolean deleteReviewNFile(int reviewId, int reserveId, List<ReviewImagesDTO> list) {
		// 리뷰 삭제, 파일 삭제
		boolean result = false;
		
		try {
			if(revDAO.delReview(reviewId) == 1) {
			 log.info("리뷰 삭제 완료");
				
			 if (list != null && !list.isEmpty()) {
					if(revDAO.delFiles(reviewId) > 0) {
					  log.info("파일 삭제 정보 삭제 완료");
	                  log.info("삭제할 이미지 개수: {}", list.size());
						
	                  for(ReviewImagesDTO fileName : list) {
							result = new S3ImageManager(s3Client, bucketName, fileName.getFileName()).deleteImage();
							if(result) {
								log.info("파일 삭제 완료 : "+ fileName.getFileName());
							}
						}
					}
				   }else {
					   log.info("삭제할 이미지가 없음, 리뷰만 삭제 완료");
				   }
			 if(revDAO.changeStatusCodeId(reserveId, 4) > 0){
					
				 	result=true;
					log.info("statusCode 4변경 완료");
					
					}
				}
			
			} catch (Exception e) {
				 log.error("삭제 중 오류 발생: {}", e.getMessage());
			}
			
		return result;
	}
       
	@Override
	public List<ReviewDTO> getMyReview(int userId,int page, int size) {
		ReviewPageDTO paging = ReviewPageDTO.builder().userId(userId).page(page).size(size).build();
		log.info(paging.toString());
		List<ReviewDTO> lst = revDAO.getMyReviews(paging);
		
		return lst;
	}

	@Override
	@Transactional
	public boolean updateReview(ReviewDTO modifyReview) {
		// 리뷰 수정
		boolean result = false;
		int reviewId=modifyReview.getReviewId();
	
		if(revDAO.updateReview(modifyReview) == 1) {
	
			if(modifyReview.getReviewImages() != null) {
				for(ReviewImagesDTO img : modifyReview.getReviewImages()) {
	
					int imageId=img.getImageId();
	
					if (img.getFileStatus() == ReviewImagesStatus.INSERT) {
						img.setReviewId(reviewId);
						revDAO.insertImgs(img);
	
					}else if(img.getFileStatus() == ReviewImagesStatus.DELETE) {
						revDAO.deleteImgs(imageId);
					}
				}
			}
			result = true;
		}
	
		return result;
	}

		@Override
		public List<ReserveDTO> getStatusByUserId(int userId) {
			List<ReserveDTO> reserve= revDAO.getReserveByUserId(userId);
			return reserve;
		}
		
		@Override
		public List<ReviewDTO> getUserReview(int userId) {
			List<ReviewDTO> reviews =revDAO.getUserReviews(userId);
			return reviews;
		}
		
		@Override
		public ReserveDTO getReserveInfoByReserveId(int reservationId) {
			// 예약 번호로 예약 정보 가져오기
			ReserveDTO reservation = revDAO.getReserveInfo(reservationId);
			return reservation;
		}
		
		@Override
		public List<ReviewImagesDTO> selectReviewImagesByReviewId(int reviewId) {
			// reviewId에 따른 reviewImages
			return revDAO.filesByNo(reviewId);
		}
		        
		@Override
		public int checkImageExist(int imageId) {
			// 이미지 파일이 존재하는지 확인
			return revDAO.checkIfImageExist(imageId);
		}
	
	
	}