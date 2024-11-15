package com.team.goott.owner.store.persistence;

import java.util.List;
import java.util.Map;

import com.team.goott.owner.domain.FacilityVO;
import com.team.goott.owner.domain.OwnerDTO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.domain.ScheduleVO;
import com.team.goott.owner.domain.StoreCategoryVO;
import com.team.goott.owner.domain.StoreDTO;
import com.team.goott.owner.domain.StoreImagesDTO;
import com.team.goott.owner.domain.StoreImagesVO;
import com.team.goott.owner.domain.StoreVO;

public interface OwnerStoreDAO {

	// store 테이블에 데이터 저장
	int createStore(StoreDTO store) throws Exception;
	StoreDTO login(String id, String pw);
	StoreDTO getStoreByOwnerId(int ownerId);
	boolean ownerRegister(OwnerDTO ownerDTO);

	// schedule 테이블에 데이터 저장
	int createSchedule(Map<String, Object> scheduleMap) throws Exception;;

	// category 테이블에 데이터 저장
	int createCategory(Map<String, Object> category) throws Exception;

	// facility 테이블에 데이터 저장
	int createFacility(Map<String, Object> facility) throws Exception;

	// storeImages 테이블에 데이터 저장
	int createStoreImages(StoreImagesDTO storeImages) throws Exception;

	// storeId로 가게 테이블 조회
	StoreVO getStoreById(int storeId) throws Exception;
	
	// storeId로 가게 운영시간 테이블 조회
    List<ScheduleVO> getSchedulesByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 카테고리 테이블 조회 
    List<StoreCategoryVO> getStoreCategoryByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 편의시설 테이블 조회
    List<FacilityVO> getFacilityByStoreId(int storeId) throws Exception;
    
    // storeId로 가게 이미지 테이블 조회
    List<StoreImagesVO> getStoreImagesByStoreId(int storeId) throws Exception;

	// store 테이블 수정
	int updateStore(int storeId, Map<String, Object> store) throws Exception;
	
	// schedule 테이블 수정 (store 테이블이 수정될 때 트랙잭션 처리)
    void updateSchedule(int storeId, Map<String, Object> scheduleUpdateData) throws Exception;
    
    // 요청받은 imageName이 DB에 존재할 경우 이미지 삭제처리
    int deleteStoreImagesByFileNames(int storeId, Map<String, List<String>> filesToDeleteMap) throws Exception;

    // 이미지의 수를 가져오는 메서드
	int getStoreImagesCountByStoreId(int storeId) throws Exception;

	// 수정시 원래있던 데이터 삭제
	int deleteCategory(int storeId, String categoryCodeId) throws Exception;
	
	// 수정시 원래있던 데이터 삭제
	int deleteFacility(int storeId, String facilityDeleteData) throws Exception;
	
	// storeId로 스케쥴정보 가져옴
	List<ScheduleDTO> getScheduleByStoreId(int storeId) ;
	
	// 예약 슬롯
	void batchInsertSlots(List<ReserveSlotsDTO> slotsToInsert);
	
	// 스케쥴의 요일에따른 dayCode를 가져옴
	List<ScheduleDTO> getScheduleByDayCode(int dayCode, int storeId);
	
	// storeId로 roationCode 가져옴
	int getRotationCodeIdByStoreId(int storeId);

	// 예약 슬롯 중복 체크
	List<ReserveSlotsDTO> getExistingSlots(Map<String, Object> map);
	
	// 스케쥴이 변경되면 예약슬롯을 삭제해주는 메서드
	int deleteSlotsByDayCodeId(Map<String, Object> dayCodeMap);

	// 가게가 생성될 때 스케쥴에 따라 예약슬롯을 생성해주는 메서드
	int insertReserveSlot(ReserveSlotsDTO newSlot);
	
	// 변경된 스케쥴에 따른 예약슬롯을 생성해주는 메서드
	void UpdateReserveSlot(ReserveSlotsDTO newSlot);
	
	// 삭제 예정 예약 슬롯 반환 받는 메 서 드 
	List<ReserveSlotsDTO> selectSlotsByDayCodeId(Map<String, Object> dayCodeMap);
	
	// 슬롯이 삭제되면 reserve 테이블의 status 코드를 바꿔줌
	int updateReserveStatus(Map<String, Object> updateSlotMap);
	

	

}
