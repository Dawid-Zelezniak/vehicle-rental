package com.vehicle.rental.zelezniak.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ListToPageConverter {

    private ListToPageConverter() {
    }

    public static <T> Page<T> convertToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        if (start > list.size()) return Page.empty();
        List<T> subList = list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }
}
