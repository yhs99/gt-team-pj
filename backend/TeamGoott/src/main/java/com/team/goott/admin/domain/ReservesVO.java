package com.team.goott.admin.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ReservesVO {
	private int reserveId;
	private int storeId;
	private int userId;
	private int couponId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime reserveTime;
	private String name;
	private int people;
	private ReserveStatusCode statusCodeType;
	private int statusCodeId;
	private String memo;
	private List<PayHistoryDTO> payHistories;
	private String userName;
	private String storeName;
	private Double totalDiscountPrice;
	private Double totalPriceBeforeDiscount;
	private Double totalPriceAfterDiscount;

	public void setPrices() {
		this.totalPriceBeforeDiscount = this.payHistories.stream()
				.mapToDouble(PayHistoryDTO::getTotalPrice)
				.sum();
		this.totalPriceAfterDiscount = (double) this.payHistories.stream()
				.mapToInt(PayHistoryDTO::getPayAmount)
				.sum();
		this.totalDiscountPrice = this.payHistories.stream()
			    .mapToDouble(ph -> ph.getTotalPrice() - ph.getPayAmount())
			    .sum();
		
	}
	
	public void setStatusCodeId(int statusCodeId) {
		this.statusCodeId = statusCodeId;
		setStatusCodeType(ReserveStatusCode.fromStatusCodeId(statusCodeId));
	}
	
	public void setStatusCodeType(ReserveStatusCode statusCodeType) {
		this.statusCodeType = statusCodeType;
	}
	
}
