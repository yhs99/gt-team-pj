package com.team.goott.owner.domain;

import org.apache.ibatis.annotations.Insert;

public interface OwnerStoreMapper {

    // 배치 처리를 위한 슬롯 추가
    @Insert("INSERT INTO reserveSlots (storeId, slotDatetime) VALUES (#{storeId}, #{slotDatetime})")
    void updateReserveSlot(ReserveSlotsDTO newSlot);  // 기존 메서드 이름 사용
}
