package com.team.goott.admin.domain;


public enum ReserveStatusCode {
	승인대기(1),
	예약승인(2),
	예약취소(3),
	예약완료(4),
	리뷰작성완료(5);
	
	private final int statusCodeId;
	ReserveStatusCode(int statusCodeId) {
	        this.statusCodeId = statusCodeId;
	    }

	    public int getStatusCodeId() {
	        return statusCodeId;
	    }

	    public static ReserveStatusCode fromStatusCodeId(int statusCodeId) {
	        for (ReserveStatusCode status : ReserveStatusCode.values()) {
	            if (status.getStatusCodeId() == statusCodeId) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Invalid statusCodeId: " + statusCodeId);
	    }
}
