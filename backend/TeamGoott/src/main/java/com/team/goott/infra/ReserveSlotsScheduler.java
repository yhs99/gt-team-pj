package com.team.goott.infra;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.team.goott.admin.store.persistence.AdminStoreDAO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;
import com.team.goott.owner.store.persistence.OwnerStoreDAO;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class ReserveSlotsScheduler {
	
	@Inject
	private AdminStoreDAO dao;
	
	@Inject
	private OwnerStoreDAO oDao;
	
	@Scheduled(cron = "0 0 0 * * ?")
	@Description("매일 자정마다 schedule 테이블에 저장된 가게의 open, close시간, rotation시간 및 schedule 설정에 따라 예약 테이블을 생성합니다.")
	public void reserveSlotsCreateScheduler() {
			LocalDateTime now = LocalDateTime.now().plusDays(1);
			log.info("스케쥴러 시작 ::: {} {}", now.toLocalDate().toString(), now.toLocalTime().toString());
	        LocalDateTime oneMonthLater = now.plusMonths(1);
	        // 예약 슬롯을 저장할 리스트
	        List<ReserveSlotsDTO> slotsToInsert = new ArrayList<>();
	        // 예약 슬롯 생성 기간 동안의 날짜를 반복합니다.
	        for (LocalDateTime date = now; date.isBefore(oneMonthLater); date = date.plusDays(1)) {
	            int dayCode = date.getDayOfWeek().getValue() % 7;
	            log.info("[{}] 날짜의 슬롯을 생성중입니다.", date.toLocalDate().toString());
	            // 해당 날짜에 대한 예약 슬롯을 생성
	            createSlotsForDate(date.toLocalDate(), dayCode, slotsToInsert);
	            log.info("[{}] 날짜의 슬롯 생성완료", date.toLocalDate().toString());
	        }
	        if(!slotsToInsert.isEmpty()) {
	        	dao.batchInsertSlots(slotsToInsert);
	        	log.info("{}개의 슬롯이 생성되었습니다.", slotsToInsert.size());
	        }
	        log.info("스케쥴러가 종료되었습니다.");
	}
	
    private void createSlotsForDate(LocalDate date, int dayCode, List<ReserveSlotsDTO> slotsToInsert) {
        // 스케줄에서 해당 요일에 대한 오픈/마감 시간 및 예약 간격을 가져옵니다.
        List<ScheduleDTO> schedules = dao.getScheduleByDayCode(dayCode);
        for (ScheduleDTO schedule : schedules) {
        	if(schedule.isCloseDay()) {
        		continue; // 정기휴일 날짜이므로 생성하지않음
        	}
        	try {
	            // storeId를 사용하여 rotationCodeId의 간격을 가져옵니다.
	            int intervalMinutes = dao.getRotationCodeIdByStoreId(schedule.getStoreId());
	
	            LocalTime openTime = schedule.getOpen();
	            LocalTime closeTime = schedule.getClose();
	
	            // 이미 생성된 예약 슬롯 조회
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("date", date);
	            map.put("storeId", schedule.getStoreId());
	            List<ReserveSlotsDTO> existingSlots = dao.getExistingSlots(map);
	
	            // 예약 슬롯 생성
	            for (LocalTime time = openTime; time.isBefore(closeTime); time = time.plusMinutes(intervalMinutes)) {
	            	final LocalTime currentTime = time;
	            	// 이미 존재하는 슬롯인지 확인
	                boolean exists = existingSlots.stream()
	                        .anyMatch(slot -> slot.getSlotDatetime().toLocalDate().equals(date) && 
	                                slot.getSlotDatetime().toLocalTime().equals(currentTime));
	
	                if (!exists) {
	                    // 새 슬롯을 생성
	                	ReserveSlotsDTO newSlot = new ReserveSlotsDTO();
	                    newSlot.setStoreId(schedule.getStoreId());
	                    newSlot.setSlotDatetime(LocalDateTime.of(date, time)); // LocalDateTime으로 설정
	                    slotsToInsert.add(newSlot); // 슬롯을 리스트에 추가
	                    //log.info("["+ schedule.getStoreId() +"] :: "+date.toString() + ", " + time.toString());
	                }
	            }
        	}catch (Exception e) {
        		e.printStackTrace();
                log.error("슬롯 생성중 오류 발생 storeId: {}, date: {}", schedule.getStoreId(), date);
                throw e;
			}
        }
    }

    
 // 특정 가게의 예약 슬롯을 생성하는 메서드
    public void createSlotsForStoreId(int storeId, LocalDate startDate, LocalDate endDate) {
        log.info("[{}] 가게의 슬롯을 생성중입니다.", storeId);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int dayCode = date.getDayOfWeek().getValue() % 7;
            
            log.info(storeId + "[{}] 날짜의 슬롯을 생성중입니다.", date);
//            log.info("date : " + date + "dayCode : " + dayCode);
            createSlotsForDate(date, dayCode, storeId); // storeId 추가
            log.info(storeId + "[{}] 날짜의 슬롯 생성완료", date);
        }
    }

    // 날짜에 대한 예약 슬롯 생성
    public void createSlotsForDate(LocalDate date, int dayCode, int storeId) {
    	List<ScheduleDTO> schedules = oDao.getScheduleByDayCode(dayCode, storeId);
        
        for (ScheduleDTO schedule : schedules) {
            if (schedule.isCloseDay() || schedule.getStoreId() != storeId) {
                continue; // 정기휴일 날짜이거나 해당 가게가 아닌 경우 생성하지 않음
            }
            try {
                int intervalMinutes = oDao.getRotationCodeIdByStoreId(schedule.getStoreId());
                LocalTime openTime = schedule.getOpen();
                LocalTime closeTime = schedule.getClose();

                // 유효성 검증
                if (openTime.isAfter(closeTime)) {
                    log.warn("유효하지 않은 시간 범위: {} ~ {}", openTime, closeTime);
                    throw new Exception("유효하지 않은 시간 범위입니다");
                }
                
                Map<String, Object> map = new HashMap<>();
                map.put("date", date);
                map.put("storeId", schedule.getStoreId());
                List<ReserveSlotsDTO> existingSlots = oDao.getExistingSlots(map);

                for (LocalTime time = openTime; time.isBefore(closeTime); time = time.plusMinutes(intervalMinutes)) {
                    final LocalTime currentTime = time;
                    boolean exists = existingSlots.stream()
                            .anyMatch(slot -> slot.getSlotDatetime().toLocalDate().equals(date) && 
                                              slot.getSlotDatetime().toLocalTime().equals(currentTime));

                    if (!exists) {
                        ReserveSlotsDTO newSlot = new ReserveSlotsDTO();
                        newSlot.setStoreId(schedule.getStoreId());
                        newSlot.setSlotDatetime(LocalDateTime.of(date, time)); // LocalDateTime으로 설정
                        
                        // 개별 슬롯 삽입
                        if (schedule.getDayCodeId() == dayCode) {
                        	oDao.insertReserveSlot(newSlot);
                        }
                        
                    }
                }
            } catch (Exception e) {
                log.error("슬롯 생성중 오류 발생 storeId: {}, date: {}, error: {}", schedule.getStoreId(), date);
            }
        }
    }
    
    // 특정 가게의 예약 슬롯을 생성 또는 업데이트하는 메서드
    public void updateSlotsForStore(int storeId, LocalDate startDate, LocalDate endDate, Map<String, Object> dayCodeMap) {
        log.info("[{}] 가게의 슬롯을 생성 또는 업데이트 중입니다.", storeId);

        // 시작 날짜부터 종료 날짜까지 슬롯을 생성합니다.
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int calDayCode = date.getDayOfWeek().getValue() % 7;
            int dayCode = (Integer) dayCodeMap.get("dayCodeId");
            
            
            if (dayCode == calDayCode) {
//            	log.info("스케쥴러의 calDayCode : {}", calDayCode);
//            	log.info("스케쥴러의 dayCode : {}", dayCode );
//            	log.info("스케쥴러의 storeId: {}", storeId);
//            	log.info("스케쥴러의 date : {}", date );
//            	log.info(storeId + "[{}] 날짜의 슬롯을 생성 또는 업데이트 중입니다.", date);
            	updateSlotsForDate(date, dayCode, storeId, dayCodeMap); // storeId 추가
//            	log.info(storeId + "[{}] 날짜의 슬롯 생성 또는 업데이트 완료", date);
            	
            }
        }
    }

    // 날짜에 대한 예약 슬롯 생성 또는 업데이트
    private void updateSlotsForDate(LocalDate date, int dayCode, int storeId, Map<String, Object> dayCodeMap) {
        // 해당 일자에 대한 스케줄을 가져옵니다.
//        List<ScheduleDTO> schedules = oDao.getScheduleByDayCode(dayCode, storeId);
    	ScheduleDTO schedules = (ScheduleDTO) dayCodeMap.get("newSchedule");    	
//    	boolean closeDay = (boolean) dayCodeMap.get("closeDay");
    	
        log.info("스케쥴러의 schedules 배열 :  " + schedules.toString());
        log.info("updateSlotsForDate의 storeId : " + storeId);
        // 각 스케줄에 대해 슬롯을 생성합니다.
        schedules.setStoreId(storeId);
    	log.info("스케쥴러의 schedule : " + schedules);
        // 정기휴일이거나 해당 가게의 스케줄이 아닐 경우 생략합니다.
        if (schedules.isCloseDay() || schedules.getStoreId() != storeId) {
        }
        try {
            // 슬롯 생성에 필요한 정보를 가져옵니다.
            int intervalMinutes = oDao.getRotationCodeIdByStoreId(schedules.getStoreId());
            log.info("스케쥴러 intervalMinutes : {}" , intervalMinutes);
            LocalTime openTime = schedules.getOpen();
            log.info(openTime+"");
            LocalTime closeTime = schedules.getClose();
            log.info(closeTime+"");

            // 유효성 검증: 개장 시간이 폐장 시간보다 늦을 경우 경고
            if (openTime.isAfter(closeTime)) {
                log.warn("유효하지 않은 시간 범위: {} ~ {}", openTime, closeTime);
            }
            
            // 기존 슬롯을 가져옵니다.
            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            map.put("storeId", storeId);
            List<ReserveSlotsDTO> existingSlots = oDao.getExistingSlots(map);

            // 운영시간 내 슬롯을 생성합니다.
            for (LocalTime time = openTime; time.isBefore(closeTime); time = time.plusMinutes(intervalMinutes)) {
                final LocalTime currentTime = time;

                // 이미 존재하는 슬롯인지 확인합니다.
                boolean exists = existingSlots.stream()
                        .anyMatch(slot -> slot.getSlotDatetime().toLocalDate().equals(date) &&
                                          slot.getSlotDatetime().toLocalTime().equals(currentTime));
                log.info(time + " " +closeTime + " ::" +exists);
                // 슬롯이 존재하지 않을 경우 새로운 슬롯을 생성합니다.
                if (!exists) {
                    ReserveSlotsDTO newSlot = new ReserveSlotsDTO();
                    newSlot.setStoreId(schedules.getStoreId());
                    newSlot.setSlotDatetime(LocalDateTime.of(date, time)); // LocalDateTime으로 설정
                    log.info("newSlot :: {} ", newSlot.toString());
//                        log.info("스케쥴러 newSlot : {} ", newSlot.toString());
                        oDao.UpdateReserveSlot(newSlot);
                        
//                            log.info("[{}] 새로운 슬롯 생성 완료: {}", schedule.getStoreId(), newSlot.getSlotDatetime());
                }
            }
        } catch (Exception e) {
            log.error("슬롯 생성 또는 업데이트 중 오류 발생 storeId: {}, date: {}, error: {}", schedules.getStoreId(), date);
        }
    }

}
