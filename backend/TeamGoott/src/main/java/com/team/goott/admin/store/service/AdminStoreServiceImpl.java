package com.team.goott.admin.store.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.team.goott.admin.store.persistence.AdminStoreDAO;
import com.team.goott.owner.domain.ReserveSlotsDTO;
import com.team.goott.owner.domain.ScheduleDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminStoreServiceImpl implements AdminStoreService {
	
	@Inject
	private AdminStoreDAO dao;
	
	
	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	@Description("매일 자정마다 가게의 open, close시간, rotation시간 및 schedule 설정에 따라 예약 테이블을 생성합니다.")
	public void reserveSlotsCreateScheduler() {
		LocalDateTime now = LocalDateTime.now();
		log.info("스케쥴러 시작 ::: {}", now.toString());
        LocalDateTime oneMonthLater = now.plusMonths(1);

        // 예약 슬롯 생성 기간 동안의 날짜를 반복합니다.
        for (LocalDateTime date = now; date.isBefore(oneMonthLater); date = date.plusDays(1)) {
            int dayCode = date.getDayOfWeek().getValue() % 7;

            // 해당 날짜에 대한 예약 슬롯을 생성
            createSlotsForDate(date.toLocalDate(), dayCode);
        }
        log.info("스케쥴러 종료");
	}
    private void createSlotsForDate(LocalDate date, int dayCode) {
        // 스케줄에서 해당 요일에 대한 오픈/마감 시간 및 예약 간격을 가져옵니다.
        List<ScheduleDTO> schedules = dao.getScheduleByDayCode(dayCode);

        for (ScheduleDTO schedule : schedules) {
        	if(schedule.isCloseDay()) {
        		return; // 정기휴일 날짜이므로 생성하지않음
        	}

            // storeId를 사용하여 rotationCodeId의 간격을 가져옵니다.
            int intervalMinutes = dao.getRotationCodeIdByStoreId(schedule.getStoreId());

            LocalTime openTime = schedule.getOpen();
            LocalTime closeTime = schedule.getClose();

            // 이미 생성된 예약 슬롯 조회
            List<ReserveSlotsDTO> existingSlots = dao.getExistingSlots(date);

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
                    dao.insertReserveSlot(newSlot);
                }
            }
        }
    }
}
