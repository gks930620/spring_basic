package com.shop.basic.common;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
@Getter
public class PagedResponse<T> {

    private  List<T> content;
    private  int page;           // 클라이언트 기준 (1-based)
    private  int size;
    private  int totalPages;
    private  long totalElements;
    private  boolean isFirst;
    private  boolean isLast;

    public PagedResponse(Page<T> page) {
        this.content =  page.getContent();
        this.page =   page.getNumber() + 1;    // 1-based 보정;
        this.size = page.getSize();
        this.totalPages =  page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }

}