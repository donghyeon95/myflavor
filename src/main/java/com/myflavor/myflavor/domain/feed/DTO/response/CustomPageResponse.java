package com.myflavor.myflavor.domain.feed.DTO.response;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Data;

@Data
public class CustomPageResponse<T> {
	private List<T> contents;
	private long totalElements;
	private int totalPages;
	private int number;  // 현재 페이지 번호
	private int size;    // 페이지 크기
	private boolean first;
	private boolean last;
	private int numberOfElements;  // 현재 페이지의 요소 수

	public CustomPageResponse(List<T> contents, Pageable pageable, long totalElements) {
		this.contents = contents;
		this.totalElements = totalElements;
		this.size = pageable.getPageSize();
		this.number = pageable.getPageNumber();
		this.totalPages = (int)Math.ceil((double)totalElements / this.size);
		this.first = this.number == 0;
		this.last = this.number == this.totalPages - 1;
		this.numberOfElements = contents.size();
	}

}
