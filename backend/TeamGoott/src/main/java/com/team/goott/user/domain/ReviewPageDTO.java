package com.team.goott.user.domain;
import com.team.goott.user.domain.ReviewPageDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author goott1
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ReviewPageDTO {
	
	private int storeId;
	private int userId;
	private int page;
	private int size;

	private int postTotal;//글의 전체 수
	private int pageTotal;//페이지의 전체 수 
	private int startRow;//현제페이지에서 표시되는 첫글의 인덱스
	
	//페이지네이션 블록
	private int blocks =10; //블럭 개수 
	private int currentBlock;//현재 페이지가 속한 블럭의 번호
	private int startBlock;//현재 블럭의 시작 페이지 번호
	private int endBlock;//현재 블럭의 마지막 페이지 번호
	
	
	public ReviewPageDTO (ReviewPageDTO dto) {
		this.page = dto.getPage();
		this.size = dto.getSize();
	}

	public void setStartRow() {
		this.startRow = (this.page-1) * this.size;
	}
	
	public void setPostTotal(int n) {
		this.postTotal = n;
	}
	
	public void setPageTotal() {
		if(this.postTotal % this.blocks == 0 ) {
			this.pageTotal = this.postTotal / this.blocks;
		}else {
			this.pageTotal = (this.postTotal / this.blocks) + 1;
		}
	}
	
	public void setCurrentBlock() {
		if(this.page % blocks ==0) {//10:1 20:2...
			this.currentBlock = this.page / this.blocks; 
		}else { //1:1 11:2...
			this.currentBlock = (this.page / this.blocks) +1; 
		}//block1 : 1~10 / block2 : 11~20
	}
	
	public void setStartBlock() {//1페이지부터 시작 /11/21...
		this.startBlock = (this.currentBlock - 1) * this.blocks + 1;
	}
	
	public void setEndBlock() {//1/11/21 + 9
		this.endBlock = this.startBlock + this.blocks -1;
		
		//마지막 페이지가 단위로 떨어지지 않을 떄  
		if(this.pageTotal < this.endBlock) {
			this.endBlock = this.pageTotal;
		}
	}
}
