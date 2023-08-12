package com.ercanbeyen.movieapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T, V> {
    private List<V> content;
    private int pageNumber;
    private int pageSize;
    private Sort sort;
    private int totalPage;
    private Long totalElements;

    public PageDto(Page<T> page, List<V> content) {
        this.content = content;
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.sort = page.getSort();
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }


}
